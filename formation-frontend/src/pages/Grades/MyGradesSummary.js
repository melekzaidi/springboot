import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useParams, Link } from 'react-router-dom';
import {
  fetchMyStudentGradesSummary,
  clearError,
  clearSuccess
} from '../../store/slices/gradesSlice';
import { createNote } from '../../store/slices/professorNotesSlice';

const MyGradesSummary = () => {
  const { profCourseNiveauId } = useParams();
  const dispatch = useDispatch();
  const { studentGradesSummary, loading, error, success } = useSelector(state => state.grades);
  const { user } = useSelector(state => state.auth);
  const [searchTerm, setSearchTerm] = useState('');
  const [quickNoteModal, setQuickNoteModal] = useState({ open: false, studentId: null, studentName: '' });
  const [quickNoteContent, setQuickNoteContent] = useState('');

  useEffect(() => {
    if (profCourseNiveauId) {
      dispatch(fetchMyStudentGradesSummary(profCourseNiveauId));
    }
  }, [dispatch, profCourseNiveauId]);

  useEffect(() => {
    if (error) {
      const timer = setTimeout(() => dispatch(clearError()), 5000);
      return () => clearTimeout(timer);
    }
  }, [error, dispatch]);

  useEffect(() => {
    if (success) {
      const timer = setTimeout(() => dispatch(clearSuccess()), 3000);
      return () => clearTimeout(timer);
    }
  }, [success, dispatch]);

  const filteredSummaries = (studentGradesSummary || []).filter(summary =>
    summary.studentNom?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    summary.studentPrenom?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    summary.studentMatricule?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const getMoyenneBadgeClass = (moyenne) => {
    if (moyenne === null || moyenne === undefined) return 'bg-secondary';
    return moyenne >= 10 ? 'bg-success' : 'bg-danger';
  };

  const handleQuickNote = (studentId, studentName) => {
    setQuickNoteModal({ open: true, studentId, studentName });
    setQuickNoteContent('');
  };

  const handleSaveQuickNote = async () => {
    if (!quickNoteContent.trim()) return;
    
    try {
      const noteData = {
        professorId: user.id,
        studentId: quickNoteModal.studentId,
        noteContent: quickNoteContent,
        profCourseNiveauId: parseInt(profCourseNiveauId),
        isVisibleToStudent: true
      };
      
      await dispatch(createNote(noteData)).unwrap();
      setQuickNoteModal({ open: false, studentId: null, studentName: '' });
      setQuickNoteContent('');
      
      // Show success message
      dispatch(clearError());
    } catch (error) {
      console.error('Error saving quick note:', error);
    }
  };

  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '400px' }}>
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Chargement...</span>
        </div>
      </div>
    );
  }

  const courseInfo = studentGradesSummary.length > 0 ? studentGradesSummary[0] : null;

  return (
    <div className="container-fluid">
      <div className="row">
        <div className="col-12">
          <div className="card">
            <div className="card-header">
              <div className="d-flex justify-content-between align-items-center">
                <div>
                  <h3 className="card-title mb-1">
                    Mes Notes - {courseInfo?.courseName}
                  </h3>
                  <p className="text-muted mb-0">
                    <strong>Niveau:</strong> {courseInfo?.niveauName} | 
                    <strong> Professeur:</strong> {courseInfo?.profPrenom} {courseInfo?.profNom}
                  </p>
                </div>
                <div>
                  <Link to="/grades/my-assignments" className="btn btn-secondary me-2">
                    <i className="fas fa-arrow-left"></i> Retour
                  </Link>
                  <Link
                    to={`/grades/form?profCourseNiveauId=${profCourseNiveauId}`}
                    className="btn btn-primary"
                  >
                    <i className="fas fa-plus"></i> Nouvelle Note
                  </Link>
                </div>
              </div>
            </div>
            <div className="card-body">
              {error && (
                <div className="alert alert-danger alert-dismissible fade show" role="alert">
                  {error}
                  <button 
                    type="button" 
                    className="btn-close" 
                    onClick={() => dispatch(clearError())}
                  ></button>
                </div>
              )}

              {success && (
                <div className="alert alert-success alert-dismissible fade show" role="alert">
                  {success}
                  <button 
                    type="button" 
                    className="btn-close" 
                    onClick={() => dispatch(clearSuccess())}
                  ></button>
                </div>
              )}

              <div className="alert alert-info">
                <i className="fas fa-info-circle"></i>
                <strong> Calcul de la moyenne:</strong> 0.3 × DS + 0.7 × Examen
              </div>

              {/* Search */}
              <div className="row mb-3">
                <div className="col-md-6">
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Rechercher un étudiant..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                  />
                </div>
                <div className="col-md-6">
                  <div className="text-muted">
                    {filteredSummaries.length} étudiant(s) trouvé(s)
                  </div>
                </div>
              </div>

              <div className="table-responsive">
                <table className="table table-striped table-hover">
                  <thead className="table-dark">
                    <tr>
                      <th>Étudiant</th>
                      <th>Matricule</th>
                      <th>Note DS</th>
                      <th>Note Examen</th>
                      <th>Moyenne Générale</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredSummaries.map(summary => (
                      <tr key={summary.studentId}>
                        <td>
                          <strong>{summary.studentPrenom} {summary.studentNom}</strong>
                        </td>
                        <td>
                          <span className="badge bg-secondary">{summary.studentMatricule}</span>
                        </td>
                        <td>
                          {summary.noteDS !== null ? (
                            <span className="badge bg-info fs-6">
                              {summary.noteDS}/20
                            </span>
                          ) : (
                            <span className="text-muted">-</span>
                          )}
                        </td>
                        <td>
                          {summary.noteExamen !== null ? (
                            <span className="badge bg-warning fs-6">
                              {summary.noteExamen}/20
                            </span>
                          ) : (
                            <span className="text-muted">-</span>
                          )}
                        </td>
                        <td>
                          {summary.moyenneGenerale !== null ? (
                            <span className={`badge fs-6 ${getMoyenneBadgeClass(summary.moyenneGenerale)}`}>
                              {summary.moyenneGenerale.toFixed(2)}/20
                            </span>
                          ) : (
                            <span className="text-muted">-</span>
                          )}
                        </td>
                        <td>
                          <div className="btn-group" role="group">
                            <Link
                              to={`/grades/form?profCourseNiveauId=${summary.profCourseNiveauId}&studentId=${summary.studentId}`}
                              className="btn btn-sm btn-outline-primary"
                              title="Ajouter une note"
                            >
                              <i className="fas fa-plus"></i>
                            </Link>
                            <button
                              type="button"
                              className="btn btn-sm btn-outline-success"
                              onClick={() => handleQuickNote(summary.studentId, `${summary.studentPrenom} ${summary.studentNom}`)}
                              title="Note rapide"
                            >
                              <i className="fas fa-sticky-note"></i>
                            </button>
                            <button
                              type="button"
                              className="btn btn-sm btn-outline-info"
                              data-bs-toggle="modal"
                              data-bs-target={`#detailsModal${summary.studentId}`}
                              title="Voir détails"
                            >
                              <i className="fas fa-eye"></i>
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                    {filteredSummaries.length === 0 && (
                      <tr>
                        <td colSpan="6" className="text-center text-muted">
                          <i className="fas fa-inbox fa-2x mb-2"></i><br />
                          Aucun étudiant inscrit dans cette matière
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Modals for student details */}
      {filteredSummaries.map(summary => (
        <div
          key={summary.studentId}
          className="modal fade"
          id={`detailsModal${summary.studentId}`}
          tabIndex="-1"
        >
          <div className="modal-dialog">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">
                  Détails - {summary.studentPrenom} {summary.studentNom}
                </h5>
                <button type="button" className="btn-close" data-bs-dismiss="modal"></button>
              </div>
              <div className="modal-body">
                <div className="row">
                  <div className="col-md-6">
                    <strong>Matricule:</strong> {summary.studentMatricule}
                  </div>
                  <div className="col-md-6">
                    <strong>Matière:</strong> {summary.courseName}
                  </div>
                </div>
                <hr />
                <div className="row">
                  <div className="col-md-4">
                    <div className="text-center">
                      <h6>Note DS</h6>
                      {summary.noteDS !== null ? (
                        <span className="badge bg-info fs-5">
                          {summary.noteDS}/20
                        </span>
                      ) : (
                        <span className="text-muted">Non notée</span>
                      )}
                    </div>
                  </div>
                  <div className="col-md-4">
                    <div className="text-center">
                      <h6>Note Examen</h6>
                      {summary.noteExamen !== null ? (
                        <span className="badge bg-warning fs-5">
                          {summary.noteExamen}/20
                        </span>
                      ) : (
                        <span className="text-muted">Non notée</span>
                      )}
                    </div>
                  </div>
                  <div className="col-md-4">
                    <div className="text-center">
                      <h6>Moyenne Générale</h6>
                      {summary.moyenneGenerale !== null ? (
                        <span className={`badge fs-5 ${getMoyenneBadgeClass(summary.moyenneGenerale)}`}>
                          {summary.moyenneGenerale.toFixed(2)}/20
                        </span>
                      ) : (
                        <span className="text-muted">Incomplète</span>
                      )}
                    </div>
                  </div>
                </div>
                {summary.moyenneGenerale !== null && (
                  <div className="mt-3">
                    <small className="text-muted">
                      Calcul: 0.3 × {summary.noteDS} + 0.7 × {summary.noteExamen} = {summary.moyenneGenerale.toFixed(2)}
                    </small>
                  </div>
                )}
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" data-bs-dismiss="modal">
                  Fermer
                </button>
                <Link
                  to={`/grades/form?profCourseNiveauId=${summary.profCourseNiveauId}&studentId=${summary.studentId}`}
                  className="btn btn-primary"
                >
                  Ajouter une note
                </Link>
              </div>
            </div>
          </div>
        </div>
      ))}

      {/* Quick Note Modal */}
      {quickNoteModal.open && (
        <div className="modal fade show" style={{ display: 'block' }} tabIndex="-1">
          <div className="modal-dialog">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">
                  <i className="fas fa-sticky-note me-2"></i>
                  Note rapide - {quickNoteModal.studentName}
                </h5>
                <button 
                  type="button" 
                  className="btn-close" 
                  onClick={() => setQuickNoteModal({ open: false, studentId: null, studentName: '' })}
                ></button>
              </div>
              <div className="modal-body">
                <div className="mb-3">
                  <label htmlFor="quickNoteContent" className="form-label">
                    Contenu de la note
                  </label>
                  <textarea
                    id="quickNoteContent"
                    className="form-control"
                    rows="4"
                    value={quickNoteContent}
                    onChange={(e) => setQuickNoteContent(e.target.value)}
                    placeholder="Saisissez votre note sur cet étudiant..."
                  ></textarea>
                  <div className="form-text">
                    Cette note sera visible par l'étudiant et liée à ce cours.
                  </div>
                </div>
              </div>
              <div className="modal-footer">
                <button 
                  type="button" 
                  className="btn btn-secondary"
                  onClick={() => setQuickNoteModal({ open: false, studentId: null, studentName: '' })}
                >
                  Annuler
                </button>
                <button 
                  type="button" 
                  className="btn btn-primary"
                  onClick={handleSaveQuickNote}
                  disabled={!quickNoteContent.trim()}
                >
                  <i className="fas fa-save me-1"></i>
                  Enregistrer
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
      {quickNoteModal.open && <div className="modal-backdrop fade show"></div>}
    </div>
  );
};

export default MyGradesSummary;
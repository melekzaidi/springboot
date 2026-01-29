import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate, useParams } from 'react-router-dom';
import {
  createGrade,
  createGradeForProf,
  updateGrade,
  fetchGradeTypes,
  clearError,
  clearSuccess
} from '../../store/slices/gradesSlice';
import { fetchStudents } from '../../store/slices/studentsSlice';
import { fetchProfCourseNiveaux } from '../../store/slices/professorsSlice';
import gradesAPI from '../../services/gradesAPI';

const GradeForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const dispatch = useDispatch();
  
  const { gradeTypes = [], loading, error, success } = useSelector(state => state.grades);
  const { students = [] } = useSelector(state => state.students);
  const { profCourseNiveaux = [] } = useSelector(state => state.professors);
  const { user } = useSelector(state => state.auth);

  const [formData, setFormData] = useState({
    studentId: '',
    profCourseNiveauId: '',
    gradeType: '',
    note: '',
    coefficient: '',
    dateEvaluation: new Date().toISOString().split('T')[0],
    commentaire: ''
  });

  const [isEdit, setIsEdit] = useState(false);

  useEffect(() => {
    dispatch(fetchGradeTypes());
    dispatch(fetchStudents());
    dispatch(fetchProfCourseNiveaux());
    
    // Get URL parameters for pre-filling form
    const urlParams = new URLSearchParams(window.location.search);
    const profCourseNiveauIdParam = urlParams.get('profCourseNiveauId');
    const studentIdParam = urlParams.get('studentId');
    
    if (profCourseNiveauIdParam) {
      setFormData(prev => ({
        ...prev,
        profCourseNiveauId: profCourseNiveauIdParam
      }));
    }
    
    if (studentIdParam) {
      setFormData(prev => ({
        ...prev,
        studentId: studentIdParam
      }));
    }
  }, [dispatch]);

  useEffect(() => {
    if (id) {
      setIsEdit(true);
      loadGrade(id);
    }
  }, [id]);

  useEffect(() => {
    if (success) {
      const timer = setTimeout(() => {
        dispatch(clearSuccess());
        navigate('/grades');
      }, 2000);
      return () => clearTimeout(timer);
    }
  }, [success, navigate, dispatch]);

  const loadGrade = async (gradeId) => {
    try {
      const response = await gradesAPI.getGradeById(gradeId);
      const grade = response.data.data;
      setFormData({
        studentId: grade.studentId,
        profCourseNiveauId: grade.profCourseNiveauId,
        gradeType: grade.gradeType,
        note: grade.note,
        coefficient: grade.coefficient,
        dateEvaluation: grade.dateEvaluation,
        commentaire: grade.commentaire || ''
      });
    } catch (error) {
      console.error('Erreur lors du chargement de la note:', error);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));

    // Auto-set coefficient based on grade type
    if (name === 'gradeType') {
      let coefficient = '';
      if (value === 'DS') {
        coefficient = '0.3';
      } else if (value === 'EXAMEN') {
        coefficient = '0.7';
      }
      setFormData(prev => ({
        ...prev,
        coefficient: coefficient
      }));
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    const gradeData = {
      ...formData,
      note: parseFloat(formData.note),
      coefficient: parseFloat(formData.coefficient),
      studentId: parseInt(formData.studentId),
      profCourseNiveauId: parseInt(formData.profCourseNiveauId)
    };

    if (isEdit) {
      dispatch(updateGrade({ id: parseInt(id), gradeData }));
    } else {
      // Use createGradeForProf for professors, createGrade for admins
      if (user?.role === 'PROF') {
        dispatch(createGradeForProf(gradeData));
      } else {
        dispatch(createGrade(gradeData));
      }
    }
  };

  const getGradeTypeDisplayName = (type) => {
    switch (type) {
      case 'DS':
        return 'Devoir Surveillé';
      case 'EXAMEN':
        return 'Examen';
      default:
        return type;
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

  return (
    <div className="container-fluid">
      <div className="row justify-content-center">
        <div className="col-md-8">
          <div className="card">
            <div className="card-header">
              <h3 className="card-title">
                {isEdit ? 'Modifier la Note' : 'Nouvelle Note'}
              </h3>
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

              <form onSubmit={handleSubmit}>
                <div className="row">
                  <div className="col-md-6">
                    <div className="mb-3">
                      <label htmlFor="studentId" className="form-label">Étudiant *</label>
                      <select
                        className="form-select"
                        id="studentId"
                        name="studentId"
                        value={formData.studentId}
                        onChange={handleChange}
                        required
                      >
                        <option value="">Sélectionner un étudiant</option>
                        {students && students.length > 0 ? (
                          students.map(student => (
                            <option key={student.id} value={student.id}>
                              {student.prenom} {student.nom} ({student.matricule})
                            </option>
                          ))
                        ) : (
                          <option disabled>Aucun étudiant disponible</option>
                        )}
                      </select>
                    </div>
                  </div>
                  <div className="col-md-6">
                    <div className="mb-3">
                      <label htmlFor="profCourseNiveauId" className="form-label">Matière-Niveau *</label>
                      <select
                        className="form-select"
                        id="profCourseNiveauId"
                        name="profCourseNiveauId"
                        value={formData.profCourseNiveauId}
                        onChange={handleChange}
                        required
                      >
                        <option value="">Sélectionner une matière</option>
                        {profCourseNiveaux && profCourseNiveaux.length > 0 ? (
                          profCourseNiveaux.map(pcn => (
                            <option key={pcn.id} value={pcn.id}>
                              {pcn.courseNiveau?.course?.nom || pcn.courseName} - {pcn.courseNiveau?.niveau?.nom || pcn.niveauName} ({pcn.prof?.prenom} {pcn.prof?.nom})
                            </option>
                          ))
                        ) : (
                          <option disabled>Aucune matière disponible</option>
                        )}
                      </select>
                    </div>
                  </div>
                </div>

                <div className="row">
                  <div className="col-md-4">
                    <div className="mb-3">
                      <label htmlFor="gradeType" className="form-label">Type de Note *</label>
                      <select
                        className="form-select"
                        id="gradeType"
                        name="gradeType"
                        value={formData.gradeType}
                        onChange={handleChange}
                        required
                      >
                        <option value="">Sélectionner un type</option>
                        {gradeTypes && gradeTypes.length > 0 ? (
                          gradeTypes.map(type => (
                            <option key={type} value={type}>
                              {getGradeTypeDisplayName(type)}
                            </option>
                          ))
                        ) : (
                          <option disabled>Aucun type disponible</option>
                        )}
                      </select>
                    </div>
                  </div>
                  <div className="col-md-4">
                    <div className="mb-3">
                      <label htmlFor="note" className="form-label">Note (sur 20) *</label>
                      <input
                        type="number"
                        className="form-control"
                        id="note"
                        name="note"
                        value={formData.note}
                        onChange={handleChange}
                        min="0"
                        max="20"
                        step="0.25"
                        required
                      />
                    </div>
                  </div>
                  <div className="col-md-4">
                    <div className="mb-3">
                      <label htmlFor="coefficient" className="form-label">Coefficient</label>
                      <input
                        type="number"
                        className="form-control"
                        id="coefficient"
                        name="coefficient"
                        value={formData.coefficient}
                        onChange={handleChange}
                        min="0"
                        step="0.1"
                        readOnly
                      />
                      <small className="form-text text-muted">Coefficient automatique selon le type</small>
                    </div>
                  </div>
                </div>

                <div className="row">
                  <div className="col-md-6">
                    <div className="mb-3">
                      <label htmlFor="dateEvaluation" className="form-label">Date d'Évaluation *</label>
                      <input
                        type="date"
                        className="form-control"
                        id="dateEvaluation"
                        name="dateEvaluation"
                        value={formData.dateEvaluation}
                        onChange={handleChange}
                        required
                      />
                    </div>
                  </div>
                  <div className="col-md-6">
                    <div className="mb-3">
                      <label htmlFor="commentaire" className="form-label">Commentaire</label>
                      <textarea
                        className="form-control"
                        id="commentaire"
                        name="commentaire"
                        value={formData.commentaire}
                        onChange={handleChange}
                        rows="3"
                        placeholder="Commentaire optionnel..."
                      />
                    </div>
                  </div>
                </div>

                <div className="alert alert-info">
                  <i className="fas fa-info-circle"></i>
                  <strong> Information:</strong> La moyenne générale est calculée automatiquement selon la formule: 0.3 × DS + 0.7 × Examen
                </div>

                <div className="d-flex justify-content-between">
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={() => navigate('/grades')}
                  >
                    <i className="fas fa-arrow-left"></i> Retour
                  </button>
                  <button type="submit" className="btn btn-primary" disabled={loading}>
                    <i className="fas fa-save"></i> 
                    {isEdit ? ' Modifier' : ' Enregistrer'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default GradeForm;
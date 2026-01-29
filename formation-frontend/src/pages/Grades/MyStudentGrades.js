import { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchMyGradesSummaryAsStudent, clearError, clearSuccess } from '../../store/slices/gradesSlice';

const MyStudentGrades = () => {
  const dispatch = useDispatch();
  const { studentGradesSummary = [], loading, error, success } = useSelector(state => state.grades);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    // Fetch student's own grades summary
    dispatch(fetchMyGradesSummaryAsStudent());
  }, [dispatch]);

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
    summary.courseName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    summary.niveauName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    summary.profNom?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    summary.profPrenom?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const getMoyenneBadgeClass = (moyenne) => {
    if (moyenne === null || moyenne === undefined) return 'bg-secondary';
    return moyenne >= 10 ? 'bg-success' : 'bg-danger';
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
      <div className="row">
        <div className="col-12">
          <div className="card">
            <div className="card-header">
              <h3 className="card-title">Mes Notes</h3>
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
                    placeholder="Rechercher par matière, niveau, professeur..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                  />
                </div>
                <div className="col-md-6">
                  <div className="text-muted">
                    {filteredSummaries.length} matière(s) trouvée(s)
                  </div>
                </div>
              </div>

              <div className="table-responsive">
                <table className="table table-striped table-hover">
                  <thead className="table-dark">
                    <tr>
                      <th>Matière</th>
                      <th>Niveau</th>
                      <th>Professeur</th>
                      <th>Note DS</th>
                      <th>Note Examen</th>
                      <th>Moyenne Générale</th>
                      <th>Statut</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredSummaries.map(summary => (
                      <tr key={summary.profCourseNiveauId}>
                        <td>
                          <strong>{summary.courseName}</strong>
                        </td>
                        <td>
                          <span className="badge bg-info">{summary.niveauName}</span>
                        </td>
                        <td>
                          {summary.profPrenom} {summary.profNom}
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
                          {summary.moyenneGenerale !== null ? (
                            <span className={`badge ${summary.moyenneGenerale >= 10 ? 'bg-success' : 'bg-danger'}`}>
                              {summary.moyenneGenerale >= 10 ? 'Admis' : 'Échec'}
                            </span>
                          ) : (
                            <span className="badge bg-secondary">En cours</span>
                          )}
                        </td>
                      </tr>
                    ))}
                    {filteredSummaries.length === 0 && (
                      <tr>
                        <td colSpan="7" className="text-center text-muted">
                          <i className="fas fa-inbox fa-2x mb-2"></i><br />
                          Aucune note disponible
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>

              {/* Statistics */}
              {filteredSummaries.length > 0 && (
                <div className="row mt-4">
                  <div className="col-md-3">
                    <div className="card bg-primary text-white">
                      <div className="card-body text-center">
                        <h5>Total Matières</h5>
                        <h3>{filteredSummaries.length}</h3>
                      </div>
                    </div>
                  </div>
                  <div className="col-md-3">
                    <div className="card bg-success text-white">
                      <div className="card-body text-center">
                        <h5>Admis</h5>
                        <h3>
                          {filteredSummaries.filter(s => s.moyenneGenerale !== null && s.moyenneGenerale >= 10).length}
                        </h3>
                      </div>
                    </div>
                  </div>
                  <div className="col-md-3">
                    <div className="card bg-danger text-white">
                      <div className="card-body text-center">
                        <h5>Échec</h5>
                        <h3>
                          {filteredSummaries.filter(s => s.moyenneGenerale !== null && s.moyenneGenerale < 10).length}
                        </h3>
                      </div>
                    </div>
                  </div>
                  <div className="col-md-3">
                    <div className="card bg-secondary text-white">
                      <div className="card-body text-center">
                        <h5>En cours</h5>
                        <h3>
                          {filteredSummaries.filter(s => s.moyenneGenerale === null).length}
                        </h3>
                      </div>
                    </div>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MyStudentGrades;
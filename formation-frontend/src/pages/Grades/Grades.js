import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import {
  fetchGrades,
  fetchMyGrades,
  deleteGrade,
  clearError,
  clearSuccess
} from '../../store/slices/gradesSlice';

const Grades = () => {
  const dispatch = useDispatch();
  const { grades = [], loading, error, success } = useSelector(state => state.grades);
  const { user } = useSelector(state => state.auth);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterType, setFilterType] = useState('');

  useEffect(() => {
    if (user?.role === 'ADMIN') {
      dispatch(fetchGrades());
    } else if (user?.role === 'PROF') {
      dispatch(fetchMyGrades());
    }
  }, [dispatch, user]);

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

  const handleDelete = async (id) => {
    if (window.confirm('Êtes-vous sûr de vouloir supprimer cette note ?')) {
      dispatch(deleteGrade(id));
    }
  };

  const filteredGrades = (grades || []).filter(grade => {
    const matchesSearch = 
      grade.studentNom?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      grade.studentPrenom?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      grade.studentMatricule?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      grade.courseName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      grade.niveauName?.toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesType = filterType === '' || grade.gradeType === filterType;
    
    return matchesSearch && matchesType;
  });

  const getGradeTypeBadge = (type) => {
    switch (type) {
      case 'DS':
        return 'badge bg-info';
      case 'EXAMEN':
        return 'badge bg-warning';
      default:
        return 'badge bg-secondary';
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
      <div className="row">
        <div className="col-12">
          <div className="card">
            <div className="card-header d-flex justify-content-between align-items-center">
              <h3 className="card-title">
                {user?.role === 'PROF' ? 'Mes Notes' : 'Gestion des Notes'}
              </h3>
              <div>
                {user?.role === 'PROF' && (
                  <Link to="/grades/my-assignments" className="btn btn-info me-2">
                    <i className="fas fa-clipboard-list"></i> Mes Affectations
                  </Link>
                )}
                <Link to="/grades/form" className="btn btn-primary">
                  <i className="fas fa-plus"></i> Nouvelle Note
                </Link>
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

              {user?.role === 'PROF' && (
                <div className="alert alert-info">
                  <i className="fas fa-info-circle"></i>
                  <strong> Conseil:</strong> Utilisez "Mes Affectations" pour gérer les notes par matière-niveau de manière organisée.
                </div>
              )}

              {/* Filters */}
              <div className="row mb-3">
                <div className="col-md-6">
                  <input
                    type="text"
                    className="form-control"
                    placeholder="Rechercher par étudiant, matière..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                  />
                </div>
                <div className="col-md-3">
                  <select
                    className="form-select"
                    value={filterType}
                    onChange={(e) => setFilterType(e.target.value)}
                  >
                    <option value="">Tous les types</option>
                    <option value="DS">Devoir Surveillé</option>
                    <option value="EXAMEN">Examen</option>
                  </select>
                </div>
                <div className="col-md-3">
                  <div className="text-muted">
                    {filteredGrades.length} note(s) trouvée(s)
                  </div>
                </div>
              </div>

              <div className="alert alert-info">
                <i className="fas fa-info-circle"></i>
                <strong> Calcul de la moyenne:</strong> 0.3 × DS + 0.7 × Examen
              </div>

              <div className="table-responsive">
                <table className="table table-striped table-hover">
                  <thead className="table-dark">
                    <tr>
                      <th>Étudiant</th>
                      <th>Matière</th>
                      <th>Niveau</th>
                      {user?.role === 'ADMIN' && <th>Professeur</th>}
                      <th>Type</th>
                      <th>Note</th>
                      <th>Coefficient</th>
                      <th>Date</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredGrades.map(grade => (
                      <tr key={grade.id}>
                        <td>
                          <strong>{grade.studentPrenom} {grade.studentNom}</strong><br />
                          <small className="text-muted">{grade.studentMatricule}</small>
                        </td>
                        <td>{grade.courseName}</td>
                        <td>{grade.niveauName}</td>
                        {user?.role === 'ADMIN' && <td>{grade.profPrenom} {grade.profNom}</td>}
                        <td>
                          <span className={getGradeTypeBadge(grade.gradeType)}>
                            {getGradeTypeDisplayName(grade.gradeType)}
                          </span>
                        </td>
                        <td>
                          <strong>{grade.note}/20</strong>
                        </td>
                        <td>{grade.coefficient}</td>
                        <td>{new Date(grade.dateEvaluation).toLocaleDateString('fr-FR')}</td>
                        <td>
                          <div className="btn-group" role="group">
                            <Link
                              to={`/grades/edit/${grade.id}`}
                              className="btn btn-sm btn-outline-primary"
                              title="Modifier"
                            >
                              <i className="fas fa-edit"></i>
                            </Link>
                            <button
                              className="btn btn-sm btn-outline-danger"
                              onClick={() => handleDelete(grade.id)}
                              title="Supprimer"
                            >
                              <i className="fas fa-trash"></i>
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                    {filteredGrades.length === 0 && (
                      <tr>
                        <td colSpan={user?.role === 'ADMIN' ? "9" : "8"} className="text-center text-muted">
                          <i className="fas fa-inbox fa-2x mb-2"></i><br />
                          Aucune note trouvée
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
    </div>
  );
};

export default Grades;
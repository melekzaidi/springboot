import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import {
  fetchMyProfCourseNiveaux,
  clearError,
  clearSuccess
} from '../../store/slices/gradesSlice';

const MyAssignments = () => {
  const dispatch = useDispatch();
  const { myProfCourseNiveaux, loading, error, success } = useSelector(state => state.grades);
  const { user } = useSelector(state => state.auth);

  useEffect(() => {
    dispatch(fetchMyProfCourseNiveaux());
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
              <h3 className="card-title">Mes Affectations - Gestion des Notes</h3>
              <p className="text-muted mb-0">
                <strong>Professeur:</strong> {user?.email}
              </p>
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
                <strong> Information:</strong> Cliquez sur "Gérer les Notes" pour attribuer des notes DS et Examen aux étudiants de chaque matière-niveau.
              </div>

              <div className="row">
                {myProfCourseNiveaux.map(pcn => (
                  <div key={pcn.id} className="col-md-6 col-lg-4 mb-4">
                    <div className="card h-100 border-primary">
                      <div className="card-header bg-primary text-white">
                        <h5 className="card-title mb-0">
                          <i className="fas fa-book"></i>
                          {pcn.courseName}
                        </h5>
                      </div>
                      <div className="card-body">
                        <div className="mb-3">
                          <strong>Niveau:</strong>
                          <span className="badge bg-secondary ms-2">{pcn.niveauName}</span>
                        </div>
                        <div className="mb-3">
                          <strong>Date d'affectation:</strong><br />
                          <small className="text-muted">
                            {new Date(pcn.dateAffectation).toLocaleDateString('fr-FR')}
                          </small>
                        </div>
                        <div className="mb-3">
                          <strong>Professeur:</strong><br />
                          <small className="text-muted">{pcn.profNomComplet}</small>
                        </div>
                      </div>
                      <div className="card-footer">
                        <div className="d-grid gap-2">
                          <Link
                            to={`/grades/my-summary/${pcn.id}`}
                            className="btn btn-primary"
                          >
                            <i className="fas fa-clipboard-list"></i> Gérer les Notes
                          </Link>
                          <Link
                            to={`/grades/form?profCourseNiveauId=${pcn.id}`}
                            className="btn btn-outline-success btn-sm"
                          >
                            <i className="fas fa-plus"></i> Ajouter une Note
                          </Link>
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
                
                {myProfCourseNiveaux.length === 0 && (
                  <div className="col-12">
                    <div className="text-center text-muted py-5">
                      <i className="fas fa-inbox fa-3x mb-3"></i>
                      <h4>Aucune affectation</h4>
                      <p>Vous n'avez aucune matière-niveau assignée pour le moment.</p>
                      <p>Contactez l'administrateur pour obtenir des affectations.</p>
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MyAssignments;
import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Routes, Route, useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Button,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  IconButton,
  Alert,
  CircularProgress,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Chip,
} from '@mui/material';
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Visibility as ViewIcon,
} from '@mui/icons-material';
import {
  fetchSessions,
  deleteSession,
  clearError,
} from '../../store/slices/sessionsSlice';
import SessionForm from './SessionForm';
import SessionDetails from './SessionDetails';

const SessionsList = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { sessions, loading, error } = useSelector((state) => state.sessions);
  const { user } = useSelector((state) => state.auth);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [sessionToDelete, setSessionToDelete] = useState(null);

  useEffect(() => {
    dispatch(fetchSessions());
  }, [dispatch]);

  const handleDeleteClick = (session) => {
    setSessionToDelete(session);
    setDeleteDialogOpen(true);
  };

  const handleDeleteConfirm = async () => {
    if (sessionToDelete) {
      await dispatch(deleteSession(sessionToDelete.id));
      setDeleteDialogOpen(false);
      setSessionToDelete(null);
      dispatch(fetchSessions());
    }
  };

  const handleDeleteCancel = () => {
    setDeleteDialogOpen(false);
    setSessionToDelete(null);
  };

  const formatDateTime = (dateTimeString) => {
    return new Date(dateTimeString).toLocaleString('fr-FR');
  };

  const getSessionStatus = (session) => {
    const now = new Date();
    const startTime = new Date(session.startDateTime);
    const endTime = new Date(session.endDateTime);

    if (now < startTime) {
      return { label: 'À venir', color: 'info' };
    } else if (now >= startTime && now <= endTime) {
      return { label: 'En cours', color: 'success' };
    } else {
      return { label: 'Terminée', color: 'default' };
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4">
          Gestion des Séances
        </Typography>
        {(user?.role === 'ADMIN' || user?.role === 'PROF') && (
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => navigate('/sessions/new')}
          >
            Créer une séance
          </Button>
        )}
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => dispatch(clearError())}>
          {error}
        </Alert>
      )}

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Date/Heure début</TableCell>
              <TableCell>Date/Heure fin</TableCell>
              <TableCell>Professeur</TableCell>
              <TableCell>Cours</TableCell>
              <TableCell>Niveau</TableCell>
              <TableCell>Statut</TableCell>
              <TableCell align="center">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {sessions.map((session) => {
              const status = getSessionStatus(session);
              return (
                <TableRow key={session.id}>
                  <TableCell>{formatDateTime(session.startDateTime)}</TableCell>
                  <TableCell>{formatDateTime(session.endDateTime)}</TableCell>
                  <TableCell>{session.profNom}</TableCell>
                  <TableCell>{session.courseName}</TableCell>
                  <TableCell>{session.niveauName}</TableCell>
                  <TableCell>
                    <Chip label={status.label} color={status.color} size="small" />
                  </TableCell>
                  <TableCell align="center">
                    <IconButton
                      color="info"
                      onClick={() => navigate(`/sessions/view/${session.id}`)}
                    >
                      <ViewIcon />
                    </IconButton>
                    {(user?.role === 'ADMIN' || user?.role === 'PROF') && (
                      <>
                        <IconButton
                          color="primary"
                          onClick={() => navigate(`/sessions/edit/${session.id}`)}
                        >
                          <EditIcon />
                        </IconButton>
                        <IconButton
                          color="error"
                          onClick={() => handleDeleteClick(session)}
                        >
                          <DeleteIcon />
                        </IconButton>
                      </>
                    )}
                  </TableCell>
                </TableRow>
              );
            })}
            {sessions.length === 0 && (
              <TableRow>
                <TableCell colSpan={7} align="center">
                  <Typography variant="body2" color="textSecondary">
                    Aucune séance trouvée
                  </Typography>
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Delete Confirmation Dialog */}
      <Dialog open={deleteDialogOpen} onClose={handleDeleteCancel}>
        <DialogTitle>Confirmer la suppression</DialogTitle>
        <DialogContent>
          <Typography>
            Êtes-vous sûr de vouloir supprimer cette séance ?
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDeleteCancel}>Annuler</Button>
          <Button onClick={handleDeleteConfirm} color="error" variant="contained">
            Supprimer
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

const Sessions = () => {
  return (
    <Routes>
      <Route path="/" element={<SessionsList />} />
      <Route path="/new" element={<SessionForm />} />
      <Route path="/edit/:id" element={<SessionForm />} />
      <Route path="/view/:id" element={<SessionDetails />} />
    </Routes>
  );
};

export default Sessions;
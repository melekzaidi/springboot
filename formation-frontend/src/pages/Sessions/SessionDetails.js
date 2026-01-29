import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Paper,
  Button,
  Alert,
  CircularProgress,
  Grid,
  Card,
  CardContent,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Checkbox,
  Chip,
} from '@mui/material';
import {
  ArrowBack as ArrowBackIcon,
  Edit as EditIcon,
} from '@mui/icons-material';
import {
  fetchSessionById,
  clearError,
} from '../../store/slices/sessionsSlice';
import attendanceAPI from '../../services/attendanceAPI';

const SessionDetails = () => {
  const { id } = useParams();
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { currentSession, loading, error } = useSelector((state) => state.sessions);
  const { user } = useSelector((state) => state.auth);
  
  const [attendances, setAttendances] = useState([]);
  const [attendanceLoading, setAttendanceLoading] = useState(false);
  const [attendanceError, setAttendanceError] = useState(null);

  useEffect(() => {
    if (id) {
      dispatch(fetchSessionById(id));
      loadAttendances();
    }

    return () => {
      dispatch(clearError());
    };
  }, [dispatch, id]);

  const loadAttendances = async () => {
    try {
      setAttendanceLoading(true);
      const response = await attendanceAPI.getBySession(id);
      setAttendances(response.data || []);
    } catch (error) {
      setAttendanceError('Erreur lors du chargement des présences');
    } finally {
      setAttendanceLoading(false);
    }
  };

  const handleAttendanceChange = async (studentId, isAbsent) => {
    try {
      await attendanceAPI.markAttendance(id, {
        studentId,
        isAbsent,
        comment: ''
      });
      loadAttendances(); // Reload attendances
    } catch (error) {
      setAttendanceError('Erreur lors de la mise à jour de la présence');
    }
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

  if (!currentSession) {
    return (
      <Box>
        <Alert severity="error">Séance non trouvée</Alert>
      </Box>
    );
  }

  const status = getSessionStatus(currentSession);
  const canEditAttendance = user?.role === 'PROF' || user?.role === 'ADMIN';

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <Button
            startIcon={<ArrowBackIcon />}
            onClick={() => navigate('/sessions')}
          >
            Retour
          </Button>
          <Typography variant="h4">
            Détails de la séance
          </Typography>
        </Box>
        {canEditAttendance && (
          <Button
            variant="contained"
            startIcon={<EditIcon />}
            onClick={() => navigate(`/sessions/edit/${id}`)}
          >
            Modifier
          </Button>
        )}
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => dispatch(clearError())}>
          {error}
        </Alert>
      )}

      {attendanceError && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setAttendanceError(null)}>
          {attendanceError}
        </Alert>
      )}

      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Informations de la séance
              </Typography>
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="textSecondary">
                  Statut
                </Typography>
                <Chip label={status.label} color={status.color} size="small" />
              </Box>
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="textSecondary">
                  Date et heure de début
                </Typography>
                <Typography variant="body1">
                  {formatDateTime(currentSession.startDateTime)}
                </Typography>
              </Box>
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="textSecondary">
                  Date et heure de fin
                </Typography>
                <Typography variant="body1">
                  {formatDateTime(currentSession.endDateTime)}
                </Typography>
              </Box>
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="textSecondary">
                  Professeur
                </Typography>
                <Typography variant="body1">
                  {currentSession.profNom}
                </Typography>
              </Box>
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="textSecondary">
                  Cours
                </Typography>
                <Typography variant="body1">
                  {currentSession.courseName}
                </Typography>
              </Box>
              <Box sx={{ mb: 2 }}>
                <Typography variant="body2" color="textSecondary">
                  Niveau
                </Typography>
                <Typography variant="body1">
                  {currentSession.niveauName}
                </Typography>
              </Box>
              {currentSession.description && (
                <Box>
                  <Typography variant="body2" color="textSecondary">
                    Description
                  </Typography>
                  <Typography variant="body1">
                    {currentSession.description}
                  </Typography>
                </Box>
              )}
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Présences
              </Typography>
              
              {attendanceLoading ? (
                <Box display="flex" justifyContent="center" p={2}>
                  <CircularProgress />
                </Box>
              ) : (
                <TableContainer>
                  <Table size="small">
                    <TableHead>
                      <TableRow>
                        <TableCell>Étudiant</TableCell>
                        <TableCell>Matricule</TableCell>
                        <TableCell align="center">Présent</TableCell>
                        <TableCell align="center">Absent</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {attendances.map((attendance) => (
                        <TableRow key={attendance.id}>
                          <TableCell>
                            {attendance.studentNomComplet}
                          </TableCell>
                          <TableCell>
                            {attendance.studentMatricule}
                          </TableCell>
                          <TableCell align="center">
                            <Checkbox
                              checked={!attendance.isAbsent}
                              onChange={(e) => canEditAttendance && handleAttendanceChange(attendance.studentId, !e.target.checked)}
                              disabled={!canEditAttendance}
                              color="success"
                            />
                          </TableCell>
                          <TableCell align="center">
                            <Checkbox
                              checked={attendance.isAbsent}
                              onChange={(e) => canEditAttendance && handleAttendanceChange(attendance.studentId, e.target.checked)}
                              disabled={!canEditAttendance}
                              color="error"
                            />
                          </TableCell>
                        </TableRow>
                      ))}
                      {attendances.length === 0 && (
                        <TableRow>
                          <TableCell colSpan={4} align="center">
                            <Typography variant="body2" color="textSecondary">
                              Aucune présence enregistrée
                            </Typography>
                          </TableCell>
                        </TableRow>
                      )}
                    </TableBody>
                  </Table>
                </TableContainer>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default SessionDetails;
import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import {
  Box,
  Grid,
  Paper,
  Typography,
  Card,
  CardContent,
  CardActions,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  IconButton,
  Alert,
  CircularProgress,
} from '@mui/material';
import {
  People as PeopleIcon,
  School as SchoolIcon,
  Book as BookIcon,
  Event as EventIcon,
  Grade as GradeIcon,
  Logout as LogoutIcon,
} from '@mui/icons-material';
import { fetchStudentCount } from '../../store/slices/studentsSlice';
import { fetchProfessors } from '../../store/slices/professorsSlice';
import { fetchCourses } from '../../store/slices/coursesSlice';
import { fetchSessions } from '../../store/slices/sessionsSlice';
import { fetchMyStudentGrades } from '../../store/slices/gradesSlice';
import { logout } from '../../store/slices/authSlice';
import { useNavigate } from 'react-router-dom';

const StatCard = ({ title, value, icon, color, action }) => (
  <Card sx={{ height: '100%' }}>
    <CardContent>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
        <Box
          sx={{
            backgroundColor: color,
            borderRadius: '50%',
            p: 1,
            mr: 2,
            color: 'white',
          }}
        >
          {icon}
        </Box>
        <Typography variant="h6" component="div">
          {title}
        </Typography>
      </Box>
      <Typography variant="h4" component="div" color="primary">
        {value}
      </Typography>
    </CardContent>
    {action && (
      <CardActions>
        {action}
      </CardActions>
    )}
  </Card>
);

const Dashboard = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { user } = useSelector((state) => state.auth);
  const { totalCount: studentCount } = useSelector((state) => state.students);
  const { professors } = useSelector((state) => state.professors);
  const { courses } = useSelector((state) => state.courses);
  const { sessions } = useSelector((state) => state.sessions);
  const { grades: myGrades, loading: gradesLoading, error: gradesError } = useSelector((state) => state.grades);

  useEffect(() => {
    if (user?.role === 'ADMIN') {
      dispatch(fetchStudentCount());
      dispatch(fetchProfessors());
      dispatch(fetchCourses());
      dispatch(fetchSessions());
    } else if (user?.role === 'STUDENT') {
      // For students, load their grades and sessions
      dispatch(fetchMyStudentGrades());
      dispatch(fetchSessions());
    } else {
      // For PROF, load their specific sessions
      dispatch(fetchSessions());
    }
  }, [dispatch, user]);

  const getWelcomeMessage = () => {
    const hour = new Date().getHours();
    let greeting = 'Bonjour';
    
    if (hour < 12) greeting = 'Bonjour';
    else if (hour < 18) greeting = 'Bon après-midi';
    else greeting = 'Bonsoir';
    
    return `${greeting}, ${user?.email}`;
  };

  const getRoleDisplay = () => {
    switch (user?.role) {
      case 'ADMIN': return 'Administrateur';
      case 'PROF': return 'Professeur';
      case 'STUDENT': return 'Étudiant';
      default: return user?.role;
    }
  };

  const getTodaySessions = () => {
    const today = new Date().toDateString();
    return sessions.filter(session => {
      const sessionDate = new Date(session.startDateTime).toDateString();
      return sessionDate === today;
    });
  };

  const getUpcomingSessions = () => {
    const now = new Date();
    return sessions.filter(session => {
      const sessionStart = new Date(session.startDateTime);
      return sessionStart > now;
    }).slice(0, 5); // Show next 5 sessions
  };

  const handleLogout = async () => {
    try {
      await dispatch(logout()).unwrap();
      navigate('/login');
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  const calculateGradeAverage = (grades) => {
    if (!grades || grades.length === 0) return 0;
    const total = grades.reduce((sum, grade) => sum + grade.note, 0);
    return (total / grades.length).toFixed(2);
  };

  const getGradeColor = (note) => {
    if (note >= 16) return 'success';
    if (note >= 14) return 'info';
    if (note >= 12) return 'warning';
    if (note >= 10) return 'secondary';
    return 'error';
  };

  const todaySessions = getTodaySessions();
  const upcomingSessions = getUpcomingSessions();

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Tableau de bord
      </Typography>
      
      <Paper sx={{ p: 3, mb: 3 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Box>
            <Typography variant="h5" gutterBottom>
              {getWelcomeMessage()}
            </Typography>
            <Typography variant="body1" color="textSecondary">
              Connecté en tant que: {getRoleDisplay()}
            </Typography>
          </Box>
          {user?.role === 'ADMIN' && (
            <Button
              variant="outlined"
              color="error"
              startIcon={<LogoutIcon />}
              onClick={handleLogout}
            >
              Se déconnecter
            </Button>
          )}
        </Box>
      </Paper>

      {user?.role === 'ADMIN' && (
        <Grid container spacing={3} sx={{ mb: 3 }}>
          <Grid item xs={12} sm={6} md={3}>
            <StatCard
              title="Étudiants"
              value={studentCount || 0}
              icon={<PeopleIcon />}
              color="#1976d2"
              action={
                <Button size="small" href="/students">
                  Voir tous
                </Button>
              }
            />
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <StatCard
              title="Professeurs"
              value={professors.length || 0}
              icon={<SchoolIcon />}
              color="#388e3c"
              action={
                <Button size="small" href="/professors">
                  Voir tous
                </Button>
              }
            />
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <StatCard
              title="Cours"
              value={courses.length || 0}
              icon={<BookIcon />}
              color="#f57c00"
              action={
                <Button size="small" href="/courses">
                  Voir tous
                </Button>
              }
            />
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <StatCard
              title="Séances"
              value={sessions.length || 0}
              icon={<EventIcon />}
              color="#7b1fa2"
              action={
                <Button size="small" href="/sessions">
                  Voir toutes
                </Button>
              }
            />
          </Grid>
        </Grid>
      )}

      {user?.role === 'STUDENT' && (
        <Grid container spacing={3} sx={{ mb: 3 }}>
          <Grid item xs={12}>
            <Paper sx={{ p: 3 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                <GradeIcon sx={{ mr: 2, color: 'primary.main' }} />
                <Typography variant="h6">
                  Mes Notes
                </Typography>
              </Box>
              
              {gradesLoading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
                  <CircularProgress />
                </Box>
              ) : gradesError ? (
                <Alert severity="error" sx={{ mb: 2 }}>
                  {gradesError}
                </Alert>
              ) : myGrades && myGrades.length > 0 ? (
                <>
                  <Box sx={{ mb: 3 }}>
                    <Typography variant="h6" color="primary">
                      Moyenne générale: {calculateGradeAverage(myGrades)}/20
                    </Typography>
                  </Box>
                  
                  <TableContainer>
                    <Table>
                      <TableHead>
                        <TableRow>
                          <TableCell>Cours</TableCell>
                          <TableCell>Niveau</TableCell>
                          <TableCell>Type</TableCell>
                          <TableCell align="center">Note</TableCell>
                          <TableCell>Date</TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {myGrades.slice(0, 10).map((grade) => (
                          <TableRow key={grade.id}>
                            <TableCell>{grade.courseName}</TableCell>
                            <TableCell>{grade.niveauName}</TableCell>
                            <TableCell>{grade.gradeType}</TableCell>
                            <TableCell align="center">
                              <Chip
                                label={`${grade.note}/20`}
                                color={getGradeColor(grade.note)}
                                variant="outlined"
                              />
                            </TableCell>
                            <TableCell>
                              {new Date(grade.dateCreated).toLocaleDateString('fr-FR')}
                            </TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </TableContainer>
                  
                  {myGrades.length > 10 && (
                    <Box sx={{ mt: 2, textAlign: 'center' }}>
                      <Button variant="outlined" href="/grades/my-grades">
                        Voir toutes mes notes ({myGrades.length})
                      </Button>
                    </Box>
                  )}
                </>
              ) : (
                <Typography variant="body2" color="textSecondary">
                  Aucune note disponible pour le moment.
                </Typography>
              )}
            </Paper>
          </Grid>
        </Grid>
      )}

      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Séances d'aujourd'hui
            </Typography>
            {todaySessions.length > 0 ? (
              <Box>
                {todaySessions.map((session) => (
                  <Box key={session.id} sx={{ mb: 2, p: 2, border: 1, borderColor: 'divider', borderRadius: 1 }}>
                    <Typography variant="subtitle1" fontWeight="medium">
                      {session.courseName} - {session.niveauName}
                    </Typography>
                    <Typography variant="body2" color="textSecondary">
                      Professeur: {session.profNom}
                    </Typography>
                    <Typography variant="body2" color="textSecondary">
                      {new Date(session.startDateTime).toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' })} - 
                      {new Date(session.endDateTime).toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' })}
                    </Typography>
                  </Box>
                ))}
              </Box>
            ) : (
              <Typography variant="body2" color="textSecondary">
                Aucune séance prévue aujourd'hui.
              </Typography>
            )}
          </Paper>
        </Grid>
        
        <Grid item xs={12} md={4}>
          <Paper sx={{ p: 3, mb: 3 }}>
            <Typography variant="h6" gutterBottom>
              Actions rapides
            </Typography>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
              <Button variant="outlined" href="/calendar" fullWidth>
                Voir le calendrier
              </Button>
              {user?.role === 'ADMIN' && (
                <>
                  <Button variant="outlined" href="/students/new" fullWidth>
                    Ajouter un étudiant
                  </Button>
                  <Button variant="outlined" href="/professors/new" fullWidth>
                    Ajouter un professeur
                  </Button>
                  <Button variant="outlined" href="/courses/new" fullWidth>
                    Ajouter un cours
                  </Button>
                </>
              )}
              {(user?.role === 'ADMIN' || user?.role === 'PROF') && (
                <Button variant="outlined" href="/sessions/new" fullWidth>
                  Créer une séance
                </Button>
              )}
            </Box>
          </Paper>

          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Prochaines séances
            </Typography>
            {upcomingSessions.length > 0 ? (
              <Box>
                {upcomingSessions.map((session) => (
                  <Box key={session.id} sx={{ mb: 1, pb: 1, borderBottom: 1, borderColor: 'divider' }}>
                    <Typography variant="body2" fontWeight="medium">
                      {session.courseName}
                    </Typography>
                    <Typography variant="caption" color="textSecondary">
                      {new Date(session.startDateTime).toLocaleDateString('fr-FR')} à{' '}
                      {new Date(session.startDateTime).toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' })}
                    </Typography>
                  </Box>
                ))}
              </Box>
            ) : (
              <Typography variant="body2" color="textSecondary">
                Aucune séance à venir.
              </Typography>
            )}
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
};

export default Dashboard;
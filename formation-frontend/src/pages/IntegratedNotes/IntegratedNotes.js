import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  Box,
  Paper,
  Typography,
  Tabs,
  Tab,
  Card,
  CardContent,
  Grid,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
  CircularProgress,
  Chip,
  Badge,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  IconButton,
  FormControlLabel,
  Switch,
  Divider,
} from '@mui/material';
import {
  Grade as GradeIcon,
  Comment as CommentIcon,
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Visibility as VisibilityIcon,
  VisibilityOff as VisibilityOffIcon,
  School as SchoolIcon,
  Person as PersonIcon,
} from '@mui/icons-material';

// Import actions
import {
  fetchStudentIntegratedData,
  fetchProfessorIntegratedData,
  createGrade,
  updateGrade,
  deleteGrade,
  createRemark,
  updateRemark,
  deleteRemark,
  fetchGradeTypes,
  fetchAllStudents,
  clearError,
  clearSuccess,
} from '../../store/slices/integratedNotesSlice';

const IntegratedNotes = () => {
  const dispatch = useDispatch();
  const { user } = useSelector((state) => state.auth);
  const { 
    studentData,
    professorData,
    students,
    loading, 
    error, 
    success 
  } = useSelector(state => state.integratedNotes);

  // State management
  const [activeTab, setActiveTab] = useState(0);
  const [openGradeDialog, setOpenGradeDialog] = useState(false);
  const [openNoteDialog, setOpenNoteDialog] = useState(false);
  const [editingGrade, setEditingGrade] = useState(null);
  const [editingNote, setEditingNote] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  // Form data
  const [gradeFormData, setGradeFormData] = useState({
    studentId: '',
    profCourseNiveauId: '',
    gradeType: 'DS',
    note: '',
    coefficient: '',
    dateEvaluation: new Date().toISOString().split('T')[0],
    commentaire: '',
  });

  const [noteFormData, setNoteFormData] = useState({
    studentId: '',
    noteContent: '',
    isVisibleToStudent: true,
    profCourseNiveauId: '',
  });

  useEffect(() => {
    if (user?.role === 'STUDENT') {
      dispatch(fetchStudentIntegratedData());
    } else if (user?.role === 'PROF') {
      dispatch(fetchProfessorIntegratedData());
      dispatch(fetchGradeTypes());
      dispatch(fetchAllStudents());
    }
  }, [dispatch, user]);

  // Clear errors after timeout
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

  // Tab change handler
  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
  };

  // Grade dialog handlers
  const handleOpenGradeDialog = (grade = null) => {
    if (grade) {
      setEditingGrade(grade);
      setGradeFormData({
        studentId: grade.studentId,
        profCourseNiveauId: grade.profCourseNiveauId,
        gradeType: grade.gradeType,
        note: grade.note,
        coefficient: grade.coefficient,
        dateEvaluation: grade.dateEvaluation,
        commentaire: grade.commentaire || '',
      });
    } else {
      setEditingGrade(null);
      setGradeFormData({
        studentId: '',
        profCourseNiveauId: '',
        gradeType: 'DS',
        note: '',
        coefficient: '',
        dateEvaluation: new Date().toISOString().split('T')[0],
        commentaire: '',
      });
    }
    setOpenGradeDialog(true);
  };

  const handleCloseGradeDialog = () => {
    setOpenGradeDialog(false);
    setEditingGrade(null);
    dispatch(clearError());
  };

  // Note dialog handlers
  const handleOpenNoteDialog = (note = null) => {
    if (note) {
      setEditingNote(note);
      setNoteFormData({
        studentId: note.studentId,
        noteContent: note.noteContent,
        isVisibleToStudent: note.isVisibleToStudent,
        profCourseNiveauId: note.profCourseNiveauId || '',
      });
    } else {
      setEditingNote(null);
      setNoteFormData({
        studentId: '',
        noteContent: '',
        isVisibleToStudent: true,
        profCourseNiveauId: '',
      });
    }
    setOpenNoteDialog(true);
  };

  const handleCloseNoteDialog = () => {
    setOpenNoteDialog(false);
    setEditingNote(null);
    dispatch(clearError());
  };

  // Form handlers
  const handleGradeFormChange = (e) => {
    const { name, value } = e.target;
    setGradeFormData(prev => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleNoteFormChange = (e) => {
    const { name, value, checked } = e.target;
    setNoteFormData(prev => ({
      ...prev,
      [name]: name === 'isVisibleToStudent' ? checked : value,
    }));
  };

  // Submit handlers
  const handleGradeSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingGrade) {
        await dispatch(updateGrade({ 
          id: editingGrade.id, 
          gradeData: gradeFormData 
        })).unwrap();
      } else {
        await dispatch(createGrade(gradeFormData)).unwrap();
      }
      handleCloseGradeDialog();
      // Refresh data
      dispatch(fetchProfessorIntegratedData());
    } catch (error) {
      console.error('Error saving grade:', error);
    }
  };

  const handleNoteSubmit = async (e) => {
    e.preventDefault();
    try {
      const noteData = {
        ...noteFormData,
        professorId: user.id,
        studentId: parseInt(noteFormData.studentId),
        profCourseNiveauId: noteFormData.profCourseNiveauId ? 
          parseInt(noteFormData.profCourseNiveauId) : null,
      };

      if (editingNote) {
        await dispatch(updateRemark({ 
          id: editingNote.id, 
          remarkData: noteData 
        })).unwrap();
      } else {
        await dispatch(createRemark(noteData)).unwrap();
      }
      handleCloseNoteDialog();
      // Refresh data
      dispatch(fetchProfessorIntegratedData());
    } catch (error) {
      console.error('Error saving note:', error);
    }
  };

  // Delete handlers
  const handleDeleteGrade = async (gradeId) => {
    if (window.confirm('Êtes-vous sûr de vouloir supprimer cette note ?')) {
      try {
        await dispatch(deleteGrade(gradeId)).unwrap();
        dispatch(fetchProfessorIntegratedData());
      } catch (error) {
        console.error('Error deleting grade:', error);
      }
    }
  };

  const handleDeleteNote = async (noteId) => {
    if (window.confirm('Êtes-vous sûr de vouloir supprimer cette remarque ?')) {
      try {
        await dispatch(deleteRemark(noteId)).unwrap();
        dispatch(fetchProfessorIntegratedData());
      } catch (error) {
        console.error('Error deleting note:', error);
      }
    }
  };

  // Utility functions
  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const getMoyenneBadgeClass = (moyenne) => {
    if (moyenne === null || moyenne === undefined) return 'secondary';
    return moyenne >= 10 ? 'success' : 'error';
  };

  const getGradeTypeColor = (gradeType) => {
    switch (gradeType) {
      case 'DS': return 'info';
      case 'EXAMEN': return 'warning';
      default: return 'default';
    }
  };

  // Filter data based on search
  const filteredGrades = (professorData.grades || []).filter(grade =>
    grade.courseName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    grade.studentName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    grade.niveauName?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const filteredNotes = (professorData.professorNotes || []).filter(note =>
    note.studentName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    note.courseName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    note.noteContent?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const filteredStudentGrades = (studentData.gradesSummary || []).filter(summary =>
    summary.courseName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    summary.niveauName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    summary.profNom?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    summary.profPrenom?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const filteredStudentNotes = (studentData.professorNotes || []).filter(note =>
    note.professorName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    note.courseName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    note.noteContent?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Box display="flex" alignItems="center" mb={3}>
        <GradeIcon sx={{ mr: 2, fontSize: 32, color: 'primary.main' }} />
        <Typography variant="h4" component="h1">
          {user?.role === 'STUDENT' ? 'Mes Notes et Remarques' : 'Gestion Notes et Remarques'}
        </Typography>
      </Box>

      {/* Error and Success Messages */}
      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {success && (
        <Alert severity="success" sx={{ mb: 2 }}>
          {success}
        </Alert>
      )}

      {/* Search */}
      <Paper sx={{ p: 2, mb: 3 }}>
        <TextField
          fullWidth
          label="Rechercher..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          placeholder={user?.role === 'STUDENT' ? 
            "Rechercher par matière, professeur..." : 
            "Rechercher par étudiant, matière..."}
        />
      </Paper>

      {/* Tabs */}
      <Paper sx={{ mb: 3 }}>
        <Tabs value={activeTab} onChange={handleTabChange}>
          {user?.role === 'STUDENT' ? (
            <>
              <Tab 
                label={
                  <Badge badgeContent={filteredStudentGrades.length} color="primary">
                    Mes Notes (Scores)
                  </Badge>
                } 
                icon={<GradeIcon />}
              />
              <Tab 
                label={
                  <Badge badgeContent={filteredStudentNotes.length} color="secondary">
                    Mes Remarques
                  </Badge>
                } 
                icon={<CommentIcon />}
              />
            </>
          ) : (
            <>
              <Tab 
                label={
                  <Badge badgeContent={filteredGrades.length} color="primary">
                    Notes (Scores)
                  </Badge>
                } 
                icon={<GradeIcon />}
              />
              <Tab 
                label={
                  <Badge badgeContent={filteredNotes.length} color="secondary">
                    Remarques
                  </Badge>
                } 
                icon={<CommentIcon />}
              />
            </>
          )}
        </Tabs>
      </Paper>

      {/* Tab Content */}
      {user?.role === 'STUDENT' ? (
        <>
          {/* Student Grades Tab */}
          {activeTab === 0 && (
            <Box>
              <Alert severity="info" sx={{ mb: 3 }}>
                <strong>Calcul de la moyenne:</strong> 0.3 × DS + 0.7 × Examen
              </Alert>

              <TableContainer component={Paper}>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>Matière</TableCell>
                      <TableCell>Niveau</TableCell>
                      <TableCell>Professeur</TableCell>
                      <TableCell>Note DS</TableCell>
                      <TableCell>Note Examen</TableCell>
                      <TableCell>Moyenne Générale</TableCell>
                      <TableCell>Statut</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {filteredStudentGrades.map(summary => (
                      <TableRow key={summary.profCourseNiveauId}>
                        <TableCell>
                          <strong>{summary.courseName}</strong>
                        </TableCell>
                        <TableCell>
                          <Chip label={summary.niveauName} color="info" size="small" />
                        </TableCell>
                        <TableCell>
                          {summary.profPrenom} {summary.profNom}
                        </TableCell>
                        <TableCell>
                          {summary.noteDS !== null ? (
                            <Chip 
                              label={`${summary.noteDS}/20`} 
                              color="info" 
                              size="small" 
                            />
                          ) : (
                            <Typography variant="body2" color="textSecondary">-</Typography>
                          )}
                        </TableCell>
                        <TableCell>
                          {summary.noteExamen !== null ? (
                            <Chip 
                              label={`${summary.noteExamen}/20`} 
                              color="warning" 
                              size="small" 
                            />
                          ) : (
                            <Typography variant="body2" color="textSecondary">-</Typography>
                          )}
                        </TableCell>
                        <TableCell>
                          {summary.moyenneGenerale !== null ? (
                            <Chip 
                              label={`${summary.moyenneGenerale.toFixed(2)}/20`} 
                              color={getMoyenneBadgeClass(summary.moyenneGenerale)}
                              size="small" 
                            />
                          ) : (
                            <Typography variant="body2" color="textSecondary">-</Typography>
                          )}
                        </TableCell>
                        <TableCell>
                          {summary.moyenneGenerale !== null ? (
                            <Chip 
                              label={summary.moyenneGenerale >= 10 ? 'Admis' : 'Échec'}
                              color={summary.moyenneGenerale >= 10 ? 'success' : 'error'}
                              size="small"
                            />
                          ) : (
                            <Chip label="En cours" color="default" size="small" />
                          )}
                        </TableCell>
                      </TableRow>
                    ))}
                    {filteredStudentGrades.length === 0 && (
                      <TableRow>
                        <TableCell colSpan={7} align="center">
                          <Typography variant="body1" color="textSecondary">
                            Aucune note disponible
                          </Typography>
                        </TableCell>
                      </TableRow>
                    )}
                  </TableBody>
                </Table>
              </TableContainer>

              {/* Statistics */}
              {filteredStudentGrades.length > 0 && (
                <Grid container spacing={2} sx={{ mt: 3 }}>
                  <Grid item xs={12} md={3}>
                    <Card sx={{ bgcolor: 'primary.main', color: 'white' }}>
                      <CardContent sx={{ textAlign: 'center' }}>
                        <Typography variant="h6">Total Matières</Typography>
                        <Typography variant="h4">{filteredStudentGrades.length}</Typography>
                      </CardContent>
                    </Card>
                  </Grid>
                  <Grid item xs={12} md={3}>
                    <Card sx={{ bgcolor: 'success.main', color: 'white' }}>
                      <CardContent sx={{ textAlign: 'center' }}>
                        <Typography variant="h6">Admis</Typography>
                        <Typography variant="h4">
                          {filteredStudentGrades.filter(s => 
                            s.moyenneGenerale !== null && s.moyenneGenerale >= 10
                          ).length}
                        </Typography>
                      </CardContent>
                    </Card>
                  </Grid>
                  <Grid item xs={12} md={3}>
                    <Card sx={{ bgcolor: 'error.main', color: 'white' }}>
                      <CardContent sx={{ textAlign: 'center' }}>
                        <Typography variant="h6">Échec</Typography>
                        <Typography variant="h4">
                          {filteredStudentGrades.filter(s => 
                            s.moyenneGenerale !== null && s.moyenneGenerale < 10
                          ).length}
                        </Typography>
                      </CardContent>
                    </Card>
                  </Grid>
                  <Grid item xs={12} md={3}>
                    <Card sx={{ bgcolor: 'grey.500', color: 'white' }}>
                      <CardContent sx={{ textAlign: 'center' }}>
                        <Typography variant="h6">En cours</Typography>
                        <Typography variant="h4">
                          {filteredStudentGrades.filter(s => s.moyenneGenerale === null).length}
                        </Typography>
                      </CardContent>
                    </Card>
                  </Grid>
                </Grid>
              )}
            </Box>
          )}

          {/* Student Notes Tab */}
          {activeTab === 1 && (
            <Box>
              {filteredStudentNotes.length === 0 ? (
                <Paper sx={{ p: 4, textAlign: 'center' }}>
                  <CommentIcon sx={{ fontSize: 64, color: 'text.secondary', mb: 2 }} />
                  <Typography variant="h6" color="textSecondary" gutterBottom>
                    Aucune remarque trouvée
                  </Typography>
                  <Typography variant="body2" color="textSecondary">
                    Vos professeurs n'ont pas encore ajouté de remarques vous concernant.
                  </Typography>
                </Paper>
              ) : (
                <Grid container spacing={2}>
                  {filteredStudentNotes.map((note) => (
                    <Grid item xs={12} key={note.id}>
                      <Card>
                        <CardContent>
                          <Box display="flex" alignItems="flex-start" mb={2}>
                            <PersonIcon sx={{ mr: 2, mt: 0.5, color: 'primary.main' }} />
                            <Box flex={1}>
                              <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={1}>
                                <Box>
                                  <Typography variant="h6" component="h3">
                                    {note.professorName}
                                  </Typography>
                                  <Typography variant="body2" color="textSecondary">
                                    Professeur
                                    {note.courseName && note.niveauName && (
                                      <> • {note.courseName} - {note.niveauName}</>
                                    )}
                                  </Typography>
                                </Box>
                                <Chip
                                  label="Remarque"
                                  color="primary"
                                  size="small"
                                  variant="outlined"
                                />
                              </Box>
                            </Box>
                          </Box>
                          
                          <Box sx={{ ml: 4 }}>
                            <Paper 
                              variant="outlined" 
                              sx={{ 
                                p: 2, 
                                mb: 2, 
                                backgroundColor: 'grey.50',
                                borderLeft: 4,
                                borderLeftColor: 'primary.main'
                              }}
                            >
                              <Typography variant="body1" sx={{ whiteSpace: 'pre-wrap' }}>
                                {note.noteContent}
                              </Typography>
                            </Paper>
                            
                            <Divider sx={{ mb: 1 }} />
                            
                            <Typography variant="caption" color="textSecondary">
                              Ajoutée le {formatDate(note.dateCreated)}
                              {note.dateUpdated !== note.dateCreated && (
                                <> • Modifiée le {formatDate(note.dateUpdated)}</>
                              )}
                            </Typography>
                          </Box>
                        </CardContent>
                      </Card>
                    </Grid>
                  ))}
                </Grid>
              )}
            </Box>
          )}
        </>
      ) : (
        <>
          {/* Professor Grades Tab */}
          {activeTab === 0 && (
            <Box>
              <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
                <Typography variant="h5">Gestion des Notes (Scores)</Typography>
                <Button
                  variant="contained"
                  color="primary"
                  startIcon={<AddIcon />}
                  onClick={() => handleOpenGradeDialog()}
                >
                  Ajouter une Note
                </Button>
              </Box>

              <TableContainer component={Paper}>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>Étudiant</TableCell>
                      <TableCell>Matière</TableCell>
                      <TableCell>Type</TableCell>
                      <TableCell>Note</TableCell>
                      <TableCell>Coefficient</TableCell>
                      <TableCell>Date</TableCell>
                      <TableCell>Actions</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {filteredGrades.map(grade => (
                      <TableRow key={grade.id}>
                        <TableCell>{grade.studentName}</TableCell>
                        <TableCell>
                          {grade.courseName} - {grade.niveauName}
                        </TableCell>
                        <TableCell>
                          <Chip 
                            label={grade.gradeType} 
                            color={getGradeTypeColor(grade.gradeType)}
                            size="small"
                          />
                        </TableCell>
                        <TableCell>
                          <strong>{grade.note}/20</strong>
                        </TableCell>
                        <TableCell>{grade.coefficient}</TableCell>
                        <TableCell>
                          {new Date(grade.dateEvaluation).toLocaleDateString('fr-FR')}
                        </TableCell>
                        <TableCell>
                          <IconButton
                            color="primary"
                            onClick={() => handleOpenGradeDialog(grade)}
                            size="small"
                          >
                            <EditIcon />
                          </IconButton>
                          <IconButton
                            color="error"
                            onClick={() => handleDeleteGrade(grade.id)}
                            size="small"
                          >
                            <DeleteIcon />
                          </IconButton>
                        </TableCell>
                      </TableRow>
                    ))}
                    {filteredGrades.length === 0 && (
                      <TableRow>
                        <TableCell colSpan={7} align="center">
                          <Typography variant="body1" color="textSecondary">
                            Aucune note trouvée
                          </Typography>
                        </TableCell>
                      </TableRow>
                    )}
                  </TableBody>
                </Table>
              </TableContainer>
            </Box>
          )}

          {/* Professor Notes Tab */}
          {activeTab === 1 && (
            <Box>
              <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
                <Typography variant="h5">Gestion des Remarques</Typography>
                <Button
                  variant="contained"
                  color="secondary"
                  startIcon={<AddIcon />}
                  onClick={() => handleOpenNoteDialog()}
                >
                  Ajouter une Remarque
                </Button>
              </Box>

              {filteredNotes.length === 0 ? (
                <Paper sx={{ p: 3, textAlign: 'center' }}>
                  <Typography variant="h6" color="textSecondary">
                    Aucune remarque trouvée
                  </Typography>
                  <Typography variant="body2" color="textSecondary" sx={{ mt: 1 }}>
                    Commencez par ajouter une remarque sur un étudiant
                  </Typography>
                </Paper>
              ) : (
                <Grid container spacing={2}>
                  {filteredNotes.map((note) => (
                    <Grid item xs={12} md={6} lg={4} key={note.id}>
                      <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
                        <CardContent sx={{ flexGrow: 1 }}>
                          <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={2}>
                            <Box>
                              <Typography variant="h6" component="h3">
                                <PersonIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
                                {note.studentName}
                              </Typography>
                              <Typography variant="body2" color="textSecondary">
                                {note.courseName && note.niveauName ? (
                                  <>
                                    <SchoolIcon sx={{ mr: 1, verticalAlign: 'middle', fontSize: 16 }} />
                                    {note.courseName} - {note.niveauName}
                                  </>
                                ) : (
                                  'Remarque générale'
                                )}
                              </Typography>
                            </Box>
                            <Chip
                              icon={note.isVisibleToStudent ? <VisibilityIcon /> : <VisibilityOffIcon />}
                              label={note.isVisibleToStudent ? 'Visible' : 'Masquée'}
                              color={note.isVisibleToStudent ? 'success' : 'default'}
                              size="small"
                            />
                          </Box>
                          
                          <Typography variant="body1" sx={{ mb: 2, whiteSpace: 'pre-wrap' }}>
                            {note.noteContent.length > 150 
                              ? `${note.noteContent.substring(0, 150)}...` 
                              : note.noteContent}
                          </Typography>
                          
                          <Divider sx={{ mb: 2 }} />
                          
                          <Typography variant="caption" color="textSecondary">
                            Créée le {formatDate(note.dateCreated)}
                            {note.dateUpdated !== note.dateCreated && (
                              <> • Modifiée le {formatDate(note.dateUpdated)}</>
                            )}
                          </Typography>
                        </CardContent>
                        
                        <Box sx={{ p: 2, pt: 0 }}>
                          <Box display="flex" gap={1}>
                            <IconButton
                              color="primary"
                              onClick={() => handleOpenNoteDialog(note)}
                              size="small"
                            >
                              <EditIcon />
                            </IconButton>
                            <IconButton
                              color="error"
                              onClick={() => handleDeleteNote(note.id)}
                              size="small"
                            >
                              <DeleteIcon />
                            </IconButton>
                          </Box>
                        </Box>
                      </Card>
                    </Grid>
                  ))}
                </Grid>
              )}
            </Box>
          )}
        </>
      )}

      {/* Grade Dialog */}
      <Dialog open={openGradeDialog} onClose={handleCloseGradeDialog} maxWidth="md" fullWidth>
        <form onSubmit={handleGradeSubmit}>
          <DialogTitle>
            {editingGrade ? 'Modifier la Note' : 'Ajouter une Note'}
          </DialogTitle>
          <DialogContent>
            <Box sx={{ pt: 1 }}>
              <Grid container spacing={2}>
                <Grid item xs={12} md={6}>
                  <FormControl fullWidth margin="normal">
                    <InputLabel>Étudiant</InputLabel>
                    <Select
                      name="studentId"
                      value={gradeFormData.studentId}
                      onChange={handleGradeFormChange}
                      required
                    >
                      {students.map((student) => (
                        <MenuItem key={student.id} value={student.id}>
                          {student.prenom} {student.nom} ({student.matricule})
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>
                <Grid item xs={12} md={6}>
                  <FormControl fullWidth margin="normal">
                    <InputLabel>Matière</InputLabel>
                    <Select
                      name="profCourseNiveauId"
                      value={gradeFormData.profCourseNiveauId}
                      onChange={handleGradeFormChange}
                      required
                    >
                      {(professorData.profCourseNiveaux || []).map((pcn) => (
                        <MenuItem key={pcn.id} value={pcn.id}>
                          {pcn.courseName} - {pcn.niveauName}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>
                <Grid item xs={12} md={4}>
                  <FormControl fullWidth margin="normal">
                    <InputLabel>Type de Note</InputLabel>
                    <Select
                      name="gradeType"
                      value={gradeFormData.gradeType}
                      onChange={handleGradeFormChange}
                      required
                    >
                      <MenuItem value="DS">DS</MenuItem>
                      <MenuItem value="EXAMEN">Examen</MenuItem>
                    </Select>
                  </FormControl>
                </Grid>
                <Grid item xs={12} md={4}>
                  <TextField
                    fullWidth
                    label="Note (/20)"
                    name="note"
                    type="number"
                    value={gradeFormData.note}
                    onChange={handleGradeFormChange}
                    required
                    margin="normal"
                    inputProps={{ min: 0, max: 20, step: 0.25 }}
                  />
                </Grid>
                <Grid item xs={12} md={4}>
                  <TextField
                    fullWidth
                    label="Coefficient"
                    name="coefficient"
                    type="number"
                    value={gradeFormData.coefficient}
                    onChange={handleGradeFormChange}
                    margin="normal"
                    inputProps={{ min: 0.1, step: 0.1 }}
                  />
                </Grid>
                <Grid item xs={12} md={6}>
                  <TextField
                    fullWidth
                    label="Date d'évaluation"
                    name="dateEvaluation"
                    type="date"
                    value={gradeFormData.dateEvaluation}
                    onChange={handleGradeFormChange}
                    required
                    margin="normal"
                    InputLabelProps={{ shrink: true }}
                  />
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Commentaire (optionnel)"
                    name="commentaire"
                    value={gradeFormData.commentaire}
                    onChange={handleGradeFormChange}
                    multiline
                    rows={3}
                    margin="normal"
                  />
                </Grid>
              </Grid>
            </Box>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCloseGradeDialog}>
              Annuler
            </Button>
            <Button type="submit" variant="contained" disabled={loading}>
              {loading ? <CircularProgress size={20} /> : (editingGrade ? 'Modifier' : 'Ajouter')}
            </Button>
          </DialogActions>
        </form>
      </Dialog>

      {/* Note Dialog */}
      <Dialog open={openNoteDialog} onClose={handleCloseNoteDialog} maxWidth="md" fullWidth>
        <form onSubmit={handleNoteSubmit}>
          <DialogTitle>
            {editingNote ? 'Modifier la Remarque' : 'Ajouter une Remarque'}
          </DialogTitle>
          <DialogContent>
            <Box sx={{ pt: 1 }}>
              <FormControl fullWidth margin="normal">
                <InputLabel>Étudiant</InputLabel>
                <Select
                  name="studentId"
                  value={noteFormData.studentId}
                  onChange={handleNoteFormChange}
                  required
                >
                  {students.map((student) => (
                    <MenuItem key={student.id} value={student.id}>
                      {student.prenom} {student.nom} ({student.matricule})
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>

              <TextField
                fullWidth
                label="Contenu de la remarque"
                name="noteContent"
                value={noteFormData.noteContent}
                onChange={handleNoteFormChange}
                required
                multiline
                rows={6}
                margin="normal"
                placeholder="Saisissez votre remarque sur cet étudiant..."
                helperText={`${noteFormData.noteContent.length}/2000 caractères`}
              />

              <FormControl fullWidth margin="normal">
                <InputLabel>Cours (optionnel)</InputLabel>
                <Select
                  name="profCourseNiveauId"
                  value={noteFormData.profCourseNiveauId}
                  onChange={handleNoteFormChange}
                >
                  <MenuItem value="">Remarque générale (aucun cours spécifique)</MenuItem>
                  {(professorData.profCourseNiveaux || []).map((pcn) => (
                    <MenuItem key={pcn.id} value={pcn.id}>
                      {pcn.courseName} - {pcn.niveauName}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>

              <FormControlLabel
                control={
                  <Switch
                    checked={noteFormData.isVisibleToStudent}
                    onChange={handleNoteFormChange}
                    name="isVisibleToStudent"
                    color="primary"
                  />
                }
                label="Visible par l'étudiant"
                sx={{ mt: 2 }}
              />
            </Box>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCloseNoteDialog}>
              Annuler
            </Button>
            <Button type="submit" variant="contained" disabled={loading}>
              {loading ? <CircularProgress size={20} /> : (editingNote ? 'Modifier' : 'Ajouter')}
            </Button>
          </DialogActions>
        </form>
      </Dialog>
    </Box>
  );
};

export default IntegratedNotes;
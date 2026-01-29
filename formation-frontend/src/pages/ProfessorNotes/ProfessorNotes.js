import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  Box,
  Paper,
  Typography,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  List,
  ListItem,
  ListItemText,
  ListItemSecondaryAction,
  IconButton,
  Chip,
  Alert,
  CircularProgress,
  FormControlLabel,
  Switch,
  Divider,
  Card,
  CardContent,
  CardActions,
  Grid,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  Tabs,
  Tab,
  Badge,
} from '@mui/material';
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Visibility as VisibilityIcon,
  VisibilityOff as VisibilityOffIcon,
  School as SchoolIcon,
  Person as PersonIcon,
  FilterList as FilterIcon,
} from '@mui/icons-material';
import {
  fetchMyNotes,
  createNote,
  updateNote,
  deleteNote,
  clearError,
  fetchNotesByStudent,
  fetchNotesByCourse,
} from '../../store/slices/professorNotesSlice';
import { fetchStudents } from '../../store/slices/studentsSlice';
import { fetchMyProfCourseNiveaux } from '../../store/slices/gradesSlice';

const ProfessorNotes = () => {
  const dispatch = useDispatch();
  const { notes, loading, error } = useSelector((state) => state.professorNotes);
  const { students } = useSelector((state) => state.students);
  const { myProfCourseNiveaux } = useSelector((state) => state.grades);
  const { user } = useSelector((state) => state.auth);

  const [open, setOpen] = useState(false);
  const [editingNote, setEditingNote] = useState(null);
  const [tabValue, setTabValue] = useState(0);
  const [filterStudent, setFilterStudent] = useState('');
  const [filterCourse, setFilterCourse] = useState('');
  const [formData, setFormData] = useState({
    studentId: '',
    noteContent: '',
    isVisibleToStudent: true,
    profCourseNiveauId: '',
  });

  useEffect(() => {
    dispatch(fetchMyNotes());
    dispatch(fetchStudents());
    dispatch(fetchMyProfCourseNiveaux());
  }, [dispatch]);

  // Filter notes based on current tab and filters
  const filteredNotes = notes.filter(note => {
    if (tabValue === 1 && !note.isVisibleToStudent) return false;
    if (tabValue === 2 && note.isVisibleToStudent) return false;
    if (filterStudent && note.studentId !== parseInt(filterStudent)) return false;
    if (filterCourse && note.profCourseNiveauId !== parseInt(filterCourse)) return false;
    return true;
  });

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const handleFilterChange = (type, value) => {
    if (type === 'student') {
      setFilterStudent(value);
      if (value) {
        dispatch(fetchNotesByStudent(parseInt(value)));
      } else {
        dispatch(fetchMyNotes());
      }
    } else if (type === 'course') {
      setFilterCourse(value);
      if (value) {
        dispatch(fetchNotesByCourse(parseInt(value)));
      } else {
        dispatch(fetchMyNotes());
      }
    }
  };

  const handleOpen = (note = null) => {
    if (note) {
      setEditingNote(note);
      setFormData({
        studentId: note.studentId,
        noteContent: note.noteContent,
        isVisibleToStudent: note.isVisibleToStudent,
        profCourseNiveauId: note.profCourseNiveauId || '',
      });
    } else {
      setEditingNote(null);
      setFormData({
        studentId: '',
        noteContent: '',
        isVisibleToStudent: true,
        profCourseNiveauId: '',
      });
    }
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setEditingNote(null);
    dispatch(clearError());
  };

  const handleChange = (e) => {
    const { name, value, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'isVisibleToStudent' ? checked : value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      const noteData = {
        ...formData,
        professorId: user.id,
        studentId: parseInt(formData.studentId),
        profCourseNiveauId: formData.profCourseNiveauId ? parseInt(formData.profCourseNiveauId) : null,
      };

      if (editingNote) {
        await dispatch(updateNote({ noteId: editingNote.id, noteData })).unwrap();
      } else {
        await dispatch(createNote(noteData)).unwrap();
      }
      
      handleClose();
    } catch (error) {
      console.error('Error saving note:', error);
    }
  };

  const handleDelete = async (noteId) => {
    if (window.confirm('Êtes-vous sûr de vouloir supprimer cette note ?')) {
      try {
        await dispatch(deleteNote(noteId)).unwrap();
      } catch (error) {
        console.error('Error deleting note:', error);
      }
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  if (loading && notes.length === 0) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" component="h1">
          Mes Notes sur les Étudiants
        </Typography>
        <Button
          variant="contained"
          color="primary"
          startIcon={<AddIcon />}
          onClick={() => handleOpen()}
        >
          Ajouter une Note
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {/* Filters */}
      <Paper sx={{ p: 2, mb: 3 }}>
        <Typography variant="h6" gutterBottom>
          <FilterIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
          Filtres
        </Typography>
        <Grid container spacing={2}>
          <Grid item xs={12} md={6}>
            <FormControl fullWidth>
              <InputLabel>Filtrer par étudiant</InputLabel>
              <Select
                value={filterStudent}
                onChange={(e) => handleFilterChange('student', e.target.value)}
                label="Filtrer par étudiant"
              >
                <MenuItem value="">Tous les étudiants</MenuItem>
                {students.map((student) => (
                  <MenuItem key={student.id} value={student.id}>
                    {student.prenom} {student.nom} ({student.matricule})
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={12} md={6}>
            <FormControl fullWidth>
              <InputLabel>Filtrer par cours</InputLabel>
              <Select
                value={filterCourse}
                onChange={(e) => handleFilterChange('course', e.target.value)}
                label="Filtrer par cours"
              >
                <MenuItem value="">Tous les cours</MenuItem>
                {myProfCourseNiveaux.map((pcn) => (
                  <MenuItem key={pcn.id} value={pcn.id}>
                    {pcn.courseName} - {pcn.niveauName}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
        </Grid>
      </Paper>

      {/* Tabs for note visibility */}
      <Paper sx={{ mb: 3 }}>
        <Tabs value={tabValue} onChange={handleTabChange} aria-label="note visibility tabs">
          <Tab 
            label={
              <Badge badgeContent={notes.length} color="primary">
                Toutes les notes
              </Badge>
            } 
          />
          <Tab 
            label={
              <Badge badgeContent={notes.filter(n => n.isVisibleToStudent).length} color="success">
                Notes visibles
              </Badge>
            } 
          />
          <Tab 
            label={
              <Badge badgeContent={notes.filter(n => !n.isVisibleToStudent).length} color="secondary">
                Notes masquées
              </Badge>
            } 
          />
        </Tabs>
      </Paper>

      {filteredNotes.length === 0 ? (
        <Paper sx={{ p: 3, textAlign: 'center' }}>
          <Typography variant="h6" color="textSecondary">
            Aucune note trouvée
          </Typography>
          <Typography variant="body2" color="textSecondary" sx={{ mt: 1 }}>
            {tabValue === 0 ? 'Commencez par ajouter une note sur un étudiant' : 
             tabValue === 1 ? 'Aucune note visible par les étudiants' :
             'Aucune note masquée'}
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
                          'Note générale'
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
                
                <CardActions>
                  <IconButton
                    color="primary"
                    onClick={() => handleOpen(note)}
                    size="small"
                  >
                    <EditIcon />
                  </IconButton>
                  <IconButton
                    color="error"
                    onClick={() => handleDelete(note.id)}
                    size="small"
                  >
                    <DeleteIcon />
                  </IconButton>
                </CardActions>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}

      {/* Add/Edit Note Dialog */}
      <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
        <form onSubmit={handleSubmit}>
          <DialogTitle>
            {editingNote ? 'Modifier la Note' : 'Ajouter une Note'}
          </DialogTitle>
          <DialogContent>
            <Box sx={{ pt: 1 }}>
              <TextField
                select
                fullWidth
                label="Étudiant"
                name="studentId"
                value={formData.studentId}
                onChange={handleChange}
                required
                margin="normal"
                SelectProps={{
                  native: true,
                }}
              >
                <option value="">Sélectionner un étudiant</option>
                {students.map((student) => (
                  <option key={student.id} value={student.id}>
                    {student.prenom} {student.nom} ({student.matricule})
                  </option>
                ))}
              </TextField>

              <TextField
                fullWidth
                label="Contenu de la note"
                name="noteContent"
                value={formData.noteContent}
                onChange={handleChange}
                required
                multiline
                rows={6}
                margin="normal"
                placeholder="Saisissez votre note sur cet étudiant..."
                helperText={`${formData.noteContent.length}/2000 caractères`}
              />

              <TextField
                select
                fullWidth
                label="Cours (optionnel)"
                name="profCourseNiveauId"
                value={formData.profCourseNiveauId}
                onChange={handleChange}
                margin="normal"
                SelectProps={{
                  native: true,
                }}
              >
                <option value="">Note générale (aucun cours spécifique)</option>
                {myProfCourseNiveaux.map((pcn) => (
                  <option key={pcn.id} value={pcn.id}>
                    {pcn.courseName} - {pcn.niveauName}
                  </option>
                ))}
              </TextField>

              <FormControlLabel
                control={
                  <Switch
                    checked={formData.isVisibleToStudent}
                    onChange={handleChange}
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
            <Button onClick={handleClose}>
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

export default ProfessorNotes;
import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  Box,
  Paper,
  Typography,
  Card,
  CardContent,
  Chip,
  Alert,
  CircularProgress,
  Divider,
  Avatar,
  Grid,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Tabs,
  Tab,
  Badge,
} from '@mui/material';
import {
  School as SchoolIcon,
  Person as PersonIcon,
  FilterList as FilterIcon,
} from '@mui/icons-material';
import { 
  fetchMyStudentNotes, 
  fetchMyStudentNotesInCourse 
} from '../../store/slices/professorNotesSlice';

const StudentNotes = () => {
  const dispatch = useDispatch();
  const { studentNotes, loading, error } = useSelector((state) => state.professorNotes);

  const [tabValue, setTabValue] = useState(0);
  const [filterCourse, setFilterCourse] = useState('');

  useEffect(() => {
    dispatch(fetchMyStudentNotes());
  }, [dispatch]);

  // Get unique courses from notes
  const uniqueCourses = studentNotes.reduce((acc, note) => {
    if (note.courseName && note.niveauName) {
      const courseKey = `${note.courseName} - ${note.niveauName}`;
      if (!acc.find(c => c.key === courseKey)) {
        acc.push({
          key: courseKey,
          id: note.profCourseNiveauId,
          name: courseKey
        });
      }
    }
    return acc;
  }, []);

  // Filter notes based on current tab and filters
  const filteredNotes = studentNotes.filter(note => {
    if (tabValue === 1 && (!note.courseName || !note.niveauName)) return false;
    if (tabValue === 2 && (note.courseName && note.niveauName)) return false;
    if (filterCourse && note.profCourseNiveauId !== parseInt(filterCourse)) return false;
    return true;
  });

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const handleFilterChange = (value) => {
    setFilterCourse(value);
    if (value) {
      dispatch(fetchMyStudentNotesInCourse(parseInt(value)));
    } else {
      dispatch(fetchMyStudentNotes());
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

  const getInitials = (name) => {
    return name
      .split(' ')
      .map(word => word.charAt(0))
      .join('')
      .toUpperCase();
  };

  if (loading && studentNotes.length === 0) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Box display="flex" alignItems="center" mb={3}>
        <SchoolIcon sx={{ mr: 2, fontSize: 32, color: 'primary.main' }} />
        <Typography variant="h4" component="h1">
          My Notes
        </Typography>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {/* Filters */}
      {uniqueCourses.length > 0 && (
        <Paper sx={{ p: 2, mb: 3 }}>
          <Typography variant="h6" gutterBottom>
            <FilterIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
            Filtres
          </Typography>
          <Grid container spacing={2}>
            <Grid item xs={12} md={6}>
              <FormControl fullWidth>
                <InputLabel>Filtrer par cours</InputLabel>
                <Select
                  value={filterCourse}
                  onChange={(e) => handleFilterChange(e.target.value)}
                  label="Filtrer par cours"
                >
                  <MenuItem value="">Tous les cours</MenuItem>
                  {uniqueCourses.map((course) => (
                    <MenuItem key={course.id} value={course.id}>
                      {course.name}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
          </Grid>
        </Paper>
      )}

      {/* Tabs for note types */}
      {studentNotes.length > 0 && (
        <Paper sx={{ mb: 3 }}>
          <Tabs value={tabValue} onChange={handleTabChange} aria-label="note type tabs">
            <Tab 
              label={
                <Badge badgeContent={studentNotes.length} color="primary">
                  Toutes les notes
                </Badge>
              } 
            />
            <Tab 
              label={
                <Badge badgeContent={studentNotes.filter(n => n.courseName && n.niveauName).length} color="info">
                  Notes de cours
                </Badge>
              } 
            />
            <Tab 
              label={
                <Badge badgeContent={studentNotes.filter(n => !n.courseName || !n.niveauName).length} color="secondary">
                  Notes générales
                </Badge>
              } 
            />
          </Tabs>
        </Paper>
      )}

      {filteredNotes.length === 0 ? (
        <Paper sx={{ p: 4, textAlign: 'center' }}>
          <SchoolIcon sx={{ fontSize: 64, color: 'text.secondary', mb: 2 }} />
          <Typography variant="h6" color="textSecondary" gutterBottom>
            Aucune note trouvée
          </Typography>
          <Typography variant="body2" color="textSecondary">
            {tabValue === 0 ? 'Vos professeurs n\'ont pas encore ajouté de notes vous concernant.' : 
             tabValue === 1 ? 'Aucune note spécifique à un cours.' :
             'Aucune note générale.'}
          </Typography>
        </Paper>
      ) : (
        <Box>
          <Typography variant="body1" color="textSecondary" sx={{ mb: 3 }}>
            {tabValue === 0 ? 'Voici les notes que vos professeurs ont ajoutées à votre sujet.' :
             tabValue === 1 ? 'Notes spécifiques à vos cours.' :
             'Notes générales de vos professeurs.'}
          </Typography>
          
          {filteredNotes.map((note) => (
            <Card key={note.id} sx={{ mb: 3, boxShadow: 2 }}>
              <CardContent>
                <Box display="flex" alignItems="flex-start" mb={2}>
                  <Avatar sx={{ bgcolor: 'primary.main', mr: 2, mt: 0.5 }}>
                    <PersonIcon />
                  </Avatar>
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
                        label="Note"
                        color="primary"
                        size="small"
                        variant="outlined"
                      />
                    </Box>
                  </Box>
                </Box>
                
                <Box sx={{ ml: 7 }}>
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
          ))}
        </Box>
      )}
    </Box>
  );
};

export default StudentNotes;
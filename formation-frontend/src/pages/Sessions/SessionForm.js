import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate, useParams } from 'react-router-dom';
import {
  Box,
  Paper,
  Typography,
  TextField,
  Button,
  Alert,
  CircularProgress,
  Grid,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
} from '@mui/material';
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import dayjs from 'dayjs';
import 'dayjs/locale/fr';
import {
  createSession,
  updateSession,
  fetchSessionById,
  clearError,
  clearCurrentSession,
} from '../../store/slices/sessionsSlice';
import profCourseNiveauxAPI from '../../services/profCourseNiveauxAPI';

dayjs.locale('fr');

const SessionForm = () => {
  const { id } = useParams();
  const isEdit = Boolean(id);
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { currentSession, loading, error } = useSelector((state) => state.sessions);

  const [formData, setFormData] = useState({
    startDateTime: dayjs(),
    endDateTime: dayjs().add(2, 'hour'),
    profCourseNiveauId: '',
    description: '',
  });

  const [formErrors, setFormErrors] = useState({});
  const [profCourseNiveaux, setProfCourseNiveaux] = useState([]);

  useEffect(() => {
    // Load prof-course-niveau assignments from API
    const loadProfCourseNiveaux = async () => {
      try {
        const response = await profCourseNiveauxAPI.getAll();
        setProfCourseNiveaux(response.data.data || []);
      } catch (error) {
        console.error('Error loading prof-course-niveau assignments:', error);
        setProfCourseNiveaux([]);
      }
    };

    loadProfCourseNiveaux();

    if (isEdit && id) {
      dispatch(fetchSessionById(id));
    } else {
      dispatch(clearCurrentSession());
    }

    return () => {
      dispatch(clearCurrentSession());
      dispatch(clearError());
    };
  }, [dispatch, id, isEdit]);

  useEffect(() => {
    if (isEdit && currentSession) {
      setFormData({
        startDateTime: currentSession.startDateTime ? dayjs(currentSession.startDateTime) : dayjs(),
        endDateTime: currentSession.endDateTime ? dayjs(currentSession.endDateTime) : dayjs().add(2, 'hour'),
        profCourseNiveauId: currentSession.profCourseNiveauId || '',
        description: currentSession.description || '',
      });
    }
  }, [currentSession, isEdit]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value,
    }));

    // Clear field error when user starts typing
    if (formErrors[name]) {
      setFormErrors(prev => ({
        ...prev,
        [name]: '',
      }));
    }
  };

  const handleDateTimeChange = (name) => (date) => {
    setFormData(prev => ({
      ...prev,
      [name]: date,
    }));
  };

  const validateForm = () => {
    const errors = {};

    if (!formData.startDateTime || !formData.startDateTime.isValid()) {
      errors.startDateTime = 'La date de début est obligatoire';
    }

    if (!formData.endDateTime || !formData.endDateTime.isValid()) {
      errors.endDateTime = 'La date de fin est obligatoire';
    }

    if (formData.startDateTime && formData.endDateTime && formData.endDateTime.isBefore(formData.startDateTime)) {
      errors.endDateTime = 'La date de fin doit être après la date de début';
    }

    if (!formData.profCourseNiveauId) {
      errors.profCourseNiveauId = 'L\'affectation professeur-cours-niveau est obligatoire';
    }

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    const sessionData = {
      ...formData,
      startDateTime: formData.startDateTime.toISOString(),
      endDateTime: formData.endDateTime.toISOString(),
    };

    try {
      if (isEdit) {
        await dispatch(updateSession({ id: parseInt(id), sessionData })).unwrap();
      } else {
        await dispatch(createSession(sessionData)).unwrap();
      }
      navigate('/sessions');
    } catch (error) {
      // Error is handled by the slice
    }
  };

  const handleCancel = () => {
    navigate('/sessions');
  };

  if (loading && isEdit) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="fr">
      <Box>
        <Typography variant="h4" gutterBottom>
          {isEdit ? 'Modifier la séance' : 'Créer une séance'}
        </Typography>

        {error && (
          <Alert severity="error" sx={{ mb: 2 }} onClose={() => dispatch(clearError())}>
            {error}
          </Alert>
        )}

        <Paper sx={{ p: 3 }}>
          <Box component="form" onSubmit={handleSubmit}>
            <Grid container spacing={3}>
              <Grid item xs={12} md={6}>
                <DateTimePicker
                  label="Date et heure de début"
                  value={formData.startDateTime}
                  onChange={handleDateTimeChange('startDateTime')}
                  slotProps={{
                    textField: {
                      fullWidth: true,
                      error: Boolean(formErrors.startDateTime),
                      helperText: formErrors.startDateTime,
                      required: true,
                    },
                  }}
                />
              </Grid>
              <Grid item xs={12} md={6}>
                <DateTimePicker
                  label="Date et heure de fin"
                  value={formData.endDateTime}
                  onChange={handleDateTimeChange('endDateTime')}
                  slotProps={{
                    textField: {
                      fullWidth: true,
                      error: Boolean(formErrors.endDateTime),
                      helperText: formErrors.endDateTime,
                      required: true,
                    },
                  }}
                />
              </Grid>
              <Grid item xs={12}>
                <FormControl fullWidth error={Boolean(formErrors.profCourseNiveauId)} required>
                  <InputLabel>Affectation Professeur-Cours-Niveau</InputLabel>
                  <Select
                    name="profCourseNiveauId"
                    value={formData.profCourseNiveauId}
                    onChange={handleChange}
                    label="Affectation Professeur-Cours-Niveau"
                  >
                    {profCourseNiveaux.map((assignment) => (
                      <MenuItem key={assignment.id} value={assignment.id}>
                        {assignment.profNom} - {assignment.courseName} ({assignment.niveauName})
                      </MenuItem>
                    ))}
                  </Select>
                  {formErrors.profCourseNiveauId && (
                    <Typography variant="caption" color="error" sx={{ mt: 1, ml: 2 }}>
                      {formErrors.profCourseNiveauId}
                    </Typography>
                  )}
                </FormControl>
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Description"
                  name="description"
                  multiline
                  rows={4}
                  value={formData.description}
                  onChange={handleChange}
                />
              </Grid>
            </Grid>

            <Box sx={{ mt: 3, display: 'flex', gap: 2 }}>
              <Button
                type="submit"
                variant="contained"
                disabled={loading}
              >
                {loading ? <CircularProgress size={24} /> : (isEdit ? 'Modifier' : 'Créer')}
              </Button>
              <Button
                variant="outlined"
                onClick={handleCancel}
                disabled={loading}
              >
                Annuler
              </Button>
            </Box>
          </Box>
        </Paper>
      </Box>
    </LocalizationProvider>
  );
};

export default SessionForm;
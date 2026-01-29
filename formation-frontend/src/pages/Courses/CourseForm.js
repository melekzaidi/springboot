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
} from '@mui/material';
import {
  createCourse,
  updateCourse,
  fetchCourseById,
  clearError,
  clearCurrentCourse,
} from '../../store/slices/coursesSlice';

const CourseForm = () => {
  const { id } = useParams();
  const isEdit = Boolean(id);
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { currentCourse, loading, error } = useSelector((state) => state.courses);

  const [formData, setFormData] = useState({
    code: '',
    nom: '',
    description: '',
  });

  const [formErrors, setFormErrors] = useState({});

  useEffect(() => {
    if (isEdit && id) {
      dispatch(fetchCourseById(id));
    } else {
      dispatch(clearCurrentCourse());
    }

    return () => {
      dispatch(clearCurrentCourse());
      dispatch(clearError());
    };
  }, [dispatch, id, isEdit]);

  useEffect(() => {
    if (isEdit && currentCourse) {
      setFormData({
        code: currentCourse.code || '',
        nom: currentCourse.nom || '',
        description: currentCourse.description || '',
      });
    }
  }, [currentCourse, isEdit]);

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

  const validateForm = () => {
    const errors = {};

    if (!formData.code.trim()) {
      errors.code = 'Le code est obligatoire';
    } else if (formData.code.length < 3) {
      errors.code = 'Le code doit avoir au moins 3 caractères';
    }

    if (!formData.nom.trim()) {
      errors.nom = 'Le nom est obligatoire';
    } else if (formData.nom.length < 3) {
      errors.nom = 'Le nom doit avoir au moins 3 caractères';
    }

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    try {
      if (isEdit) {
        await dispatch(updateCourse({ id: parseInt(id), courseData: formData })).unwrap();
      } else {
        await dispatch(createCourse(formData)).unwrap();
      }
      navigate('/courses');
    } catch (error) {
      // Error is handled by the slice
    }
  };

  const handleCancel = () => {
    navigate('/courses');
  };

  if (loading && isEdit) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        {isEdit ? 'Modifier le cours' : 'Ajouter un cours'}
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
              <TextField
                fullWidth
                label="Code"
                name="code"
                value={formData.code}
                onChange={handleChange}
                error={Boolean(formErrors.code)}
                helperText={formErrors.code}
                required
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="Nom"
                name="nom"
                value={formData.nom}
                onChange={handleChange}
                error={Boolean(formErrors.nom)}
                helperText={formErrors.nom}
                required
              />
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
              {loading ? <CircularProgress size={24} /> : (isEdit ? 'Modifier' : 'Ajouter')}
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
  );
};

export default CourseForm;
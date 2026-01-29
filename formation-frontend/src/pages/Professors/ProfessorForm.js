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
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import dayjs from 'dayjs';
import 'dayjs/locale/fr';
import {
  createProfessor,
  updateProfessor,
  fetchProfessorById,
  clearError,
  clearCurrentProfessor,
} from '../../store/slices/professorsSlice';

dayjs.locale('fr');

const ProfessorForm = () => {
  const { id } = useParams();
  const isEdit = Boolean(id);
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { currentProfessor, loading, error } = useSelector((state) => state.professors);

  const [formData, setFormData] = useState({
    matricule: '',
    nom: '',
    prenom: '',
    email: '',
    specialite: '',
    telephone: '',
    dateEmbauche: dayjs(),
  });

  const [formErrors, setFormErrors] = useState({});

  useEffect(() => {
    if (isEdit && id) {
      dispatch(fetchProfessorById(id));
    } else {
      dispatch(clearCurrentProfessor());
    }

    return () => {
      dispatch(clearCurrentProfessor());
      dispatch(clearError());
    };
  }, [dispatch, id, isEdit]);

  useEffect(() => {
    if (isEdit && currentProfessor) {
      setFormData({
        matricule: currentProfessor.matricule || '',
        nom: currentProfessor.nom || '',
        prenom: currentProfessor.prenom || '',
        email: currentProfessor.email || '',
        specialite: currentProfessor.specialite || '',
        telephone: currentProfessor.telephone || '',
        dateEmbauche: currentProfessor.dateEmbauche ? dayjs(currentProfessor.dateEmbauche) : dayjs(),
      });
    }
  }, [currentProfessor, isEdit]);

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

  const handleDateChange = (date) => {
    setFormData(prev => ({
      ...prev,
      dateEmbauche: date,
    }));
  };

  const validateForm = () => {
    const errors = {};

    if (!formData.matricule.trim()) {
      errors.matricule = 'Le matricule est obligatoire';
    } else if (formData.matricule.length < 5) {
      errors.matricule = 'Le matricule doit avoir au moins 5 caractères';
    }

    if (!formData.nom.trim()) {
      errors.nom = 'Le nom est obligatoire';
    } else if (formData.nom.length < 2) {
      errors.nom = 'Le nom doit avoir au moins 2 caractères';
    }

    if (!formData.prenom.trim()) {
      errors.prenom = 'Le prénom est obligatoire';
    } else if (formData.prenom.length < 2) {
      errors.prenom = 'Le prénom doit avoir au moins 2 caractères';
    }

    if (!formData.email.trim()) {
      errors.email = 'L\'email est obligatoire';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      errors.email = 'Format d\'email invalide';
    }

    if (!formData.specialite.trim()) {
      errors.specialite = 'La spécialité est obligatoire';
    } else if (formData.specialite.length < 2) {
      errors.specialite = 'La spécialité doit avoir au moins 2 caractères';
    }

    if (!formData.dateEmbauche || !formData.dateEmbauche.isValid()) {
      errors.dateEmbauche = 'La date d\'embauche est obligatoire';
    }

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    const professorData = {
      ...formData,
      dateEmbauche: formData.dateEmbauche.format('YYYY-MM-DD'),
    };

    try {
      if (isEdit) {
        await dispatch(updateProfessor({ id: parseInt(id), professorData })).unwrap();
      } else {
        await dispatch(createProfessor(professorData)).unwrap();
      }
      navigate('/professors');
    } catch (error) {
      // Error is handled by the slice
    }
  };

  const handleCancel = () => {
    navigate('/professors');
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
          {isEdit ? 'Modifier le professeur' : 'Ajouter un professeur'}
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
                  label="Matricule"
                  name="matricule"
                  value={formData.matricule}
                  onChange={handleChange}
                  error={Boolean(formErrors.matricule)}
                  helperText={formErrors.matricule}
                  required
                />
              </Grid>
              <Grid item xs={12} md={6}>
                <TextField
                  fullWidth
                  label="Email"
                  name="email"
                  type="email"
                  value={formData.email}
                  onChange={handleChange}
                  error={Boolean(formErrors.email)}
                  helperText={formErrors.email}
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
              <Grid item xs={12} md={6}>
                <TextField
                  fullWidth
                  label="Prénom"
                  name="prenom"
                  value={formData.prenom}
                  onChange={handleChange}
                  error={Boolean(formErrors.prenom)}
                  helperText={formErrors.prenom}
                  required
                />
              </Grid>
              <Grid item xs={12} md={6}>
                <TextField
                  fullWidth
                  label="Spécialité"
                  name="specialite"
                  value={formData.specialite}
                  onChange={handleChange}
                  error={Boolean(formErrors.specialite)}
                  helperText={formErrors.specialite}
                  required
                />
              </Grid>
              <Grid item xs={12} md={6}>
                <TextField
                  fullWidth
                  label="Téléphone"
                  name="telephone"
                  value={formData.telephone}
                  onChange={handleChange}
                />
              </Grid>
              <Grid item xs={12} md={6}>
                <DatePicker
                  label="Date d'embauche"
                  value={formData.dateEmbauche}
                  onChange={handleDateChange}
                  slotProps={{
                    textField: {
                      fullWidth: true,
                      error: Boolean(formErrors.dateEmbauche),
                      helperText: formErrors.dateEmbauche,
                      required: true,
                    },
                  }}
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
    </LocalizationProvider>
  );
};

export default ProfessorForm;
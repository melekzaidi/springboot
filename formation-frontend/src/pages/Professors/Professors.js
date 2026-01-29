import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Routes, Route, useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Button,
  TextField,
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
  InputAdornment,
} from '@mui/material';
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Search as SearchIcon,
} from '@mui/icons-material';
import {
  fetchProfessors,
  deleteProfessor,
  clearError,
} from '../../store/slices/professorsSlice';
import ProfessorForm from './ProfessorForm';

const ProfessorsList = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { professors, loading, error } = useSelector((state) => state.professors);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [professorToDelete, setProfessorToDelete] = useState(null);
  const [searchKeyword, setSearchKeyword] = useState('');

  useEffect(() => {
    dispatch(fetchProfessors());
  }, [dispatch]);

  const handleDeleteClick = (professor) => {
    setProfessorToDelete(professor);
    setDeleteDialogOpen(true);
  };

  const handleDeleteConfirm = async () => {
    if (professorToDelete) {
      await dispatch(deleteProfessor(professorToDelete.id));
      setDeleteDialogOpen(false);
      setProfessorToDelete(null);
      dispatch(fetchProfessors());
    }
  };

  const handleDeleteCancel = () => {
    setDeleteDialogOpen(false);
    setProfessorToDelete(null);
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('fr-FR');
  };

  const filteredProfessors = professors.filter(professor =>
    professor.nom.toLowerCase().includes(searchKeyword.toLowerCase()) ||
    professor.prenom.toLowerCase().includes(searchKeyword.toLowerCase()) ||
    professor.matricule.toLowerCase().includes(searchKeyword.toLowerCase()) ||
    professor.email.toLowerCase().includes(searchKeyword.toLowerCase()) ||
    professor.specialite.toLowerCase().includes(searchKeyword.toLowerCase())
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
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4">
          Gestion des Professeurs
        </Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => navigate('/professors/new')}
        >
          Ajouter un professeur
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => dispatch(clearError())}>
          {error}
        </Alert>
      )}

      <Paper sx={{ p: 2, mb: 3 }}>
        <TextField
          fullWidth
          placeholder="Rechercher un professeur..."
          value={searchKeyword}
          onChange={(e) => setSearchKeyword(e.target.value)}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon />
              </InputAdornment>
            ),
          }}
        />
      </Paper>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Matricule</TableCell>
              <TableCell>Nom complet</TableCell>
              <TableCell>Email</TableCell>
              <TableCell>Spécialité</TableCell>
              <TableCell>Date d'embauche</TableCell>
              <TableCell align="center">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {filteredProfessors.map((professor) => (
              <TableRow key={professor.id}>
                <TableCell>{professor.matricule}</TableCell>
                <TableCell>{professor.prenom} {professor.nom}</TableCell>
                <TableCell>{professor.email}</TableCell>
                <TableCell>{professor.specialite}</TableCell>
                <TableCell>{formatDate(professor.dateEmbauche)}</TableCell>
                <TableCell align="center">
                  <IconButton
                    color="primary"
                    onClick={() => navigate(`/professors/edit/${professor.id}`)}
                  >
                    <EditIcon />
                  </IconButton>
                  <IconButton
                    color="error"
                    onClick={() => handleDeleteClick(professor)}
                  >
                    <DeleteIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
            {filteredProfessors.length === 0 && (
              <TableRow>
                <TableCell colSpan={6} align="center">
                  <Typography variant="body2" color="textSecondary">
                    Aucun professeur trouvé
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
            Êtes-vous sûr de vouloir supprimer le professeur{' '}
            <strong>{professorToDelete?.prenom} {professorToDelete?.nom}</strong> ?
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

const Professors = () => {
  return (
    <Routes>
      <Route path="/" element={<ProfessorsList />} />
      <Route path="/new" element={<ProfessorForm />} />
      <Route path="/edit/:id" element={<ProfessorForm />} />
    </Routes>
  );
};

export default Professors;
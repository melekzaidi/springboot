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
  fetchNiveaux,
  deleteNiveau,
  clearError,
} from '../../store/slices/niveauxSlice';
import NiveauForm from './NiveauForm';

const NiveauxList = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { niveaux, loading, error } = useSelector((state) => state.niveaux);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [niveauToDelete, setNiveauToDelete] = useState(null);
  const [searchKeyword, setSearchKeyword] = useState('');

  useEffect(() => {
    dispatch(fetchNiveaux());
  }, [dispatch]);

  const handleDeleteClick = (niveau) => {
    setNiveauToDelete(niveau);
    setDeleteDialogOpen(true);
  };

  const handleDeleteConfirm = async () => {
    if (niveauToDelete) {
      await dispatch(deleteNiveau(niveauToDelete.id));
      setDeleteDialogOpen(false);
      setNiveauToDelete(null);
      dispatch(fetchNiveaux());
    }
  };

  const handleDeleteCancel = () => {
    setDeleteDialogOpen(false);
    setNiveauToDelete(null);
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('fr-FR');
  };

  const filteredNiveaux = niveaux.filter(niveau =>
    niveau.nom.toLowerCase().includes(searchKeyword.toLowerCase()) ||
    niveau.code.toLowerCase().includes(searchKeyword.toLowerCase()) ||
    (niveau.description && niveau.description.toLowerCase().includes(searchKeyword.toLowerCase()))
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
          Gestion des Niveaux
        </Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => navigate('/niveaux/new')}
        >
          Ajouter un niveau
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
          placeholder="Rechercher un niveau..."
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
              <TableCell>Code</TableCell>
              <TableCell>Nom</TableCell>
              <TableCell>Description</TableCell>
              <TableCell>Date de création</TableCell>
              <TableCell align="center">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {filteredNiveaux.map((niveau) => (
              <TableRow key={niveau.id}>
                <TableCell>{niveau.code}</TableCell>
                <TableCell>{niveau.nom}</TableCell>
                <TableCell>{niveau.description || '-'}</TableCell>
                <TableCell>{formatDate(niveau.dateCreation)}</TableCell>
                <TableCell align="center">
                  <IconButton
                    color="primary"
                    onClick={() => navigate(`/niveaux/edit/${niveau.id}`)}
                  >
                    <EditIcon />
                  </IconButton>
                  <IconButton
                    color="error"
                    onClick={() => handleDeleteClick(niveau)}
                  >
                    <DeleteIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
            {filteredNiveaux.length === 0 && (
              <TableRow>
                <TableCell colSpan={5} align="center">
                  <Typography variant="body2" color="textSecondary">
                    Aucun niveau trouvé
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
            Êtes-vous sûr de vouloir supprimer le niveau{' '}
            <strong>{niveauToDelete?.nom}</strong> ?
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

const Niveaux = () => {
  return (
    <Routes>
      <Route path="/" element={<NiveauxList />} />
      <Route path="/new" element={<NiveauForm />} />
      <Route path="/edit/:id" element={<NiveauForm />} />
    </Routes>
  );
};

export default Niveaux;
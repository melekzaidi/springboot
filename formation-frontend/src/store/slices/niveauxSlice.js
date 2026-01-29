import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import niveauxAPI from '../../services/niveauxAPI';

// Async thunks
export const fetchNiveaux = createAsyncThunk(
  'niveaux/fetchNiveaux',
  async (_, { rejectWithValue }) => {
    try {
      const response = await niveauxAPI.getAll();
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération des niveaux');
    }
  }
);

export const fetchNiveauById = createAsyncThunk(
  'niveaux/fetchNiveauById',
  async (id, { rejectWithValue }) => {
    try {
      const response = await niveauxAPI.getById(id);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération du niveau');
    }
  }
);

export const createNiveau = createAsyncThunk(
  'niveaux/createNiveau',
  async (niveauData, { rejectWithValue }) => {
    try {
      const response = await niveauxAPI.create(niveauData);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la création du niveau');
    }
  }
);

export const updateNiveau = createAsyncThunk(
  'niveaux/updateNiveau',
  async ({ id, niveauData }, { rejectWithValue }) => {
    try {
      const response = await niveauxAPI.update(id, niveauData);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la modification du niveau');
    }
  }
);

export const deleteNiveau = createAsyncThunk(
  'niveaux/deleteNiveau',
  async (id, { rejectWithValue }) => {
    try {
      await niveauxAPI.delete(id);
      return id;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la suppression du niveau');
    }
  }
);

const niveauxSlice = createSlice({
  name: 'niveaux',
  initialState: {
    niveaux: [],
    currentNiveau: null,
    loading: false,
    error: null,
  },
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    clearCurrentNiveau: (state) => {
      state.currentNiveau = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch Niveaux
      .addCase(fetchNiveaux.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchNiveaux.fulfilled, (state, action) => {
        state.loading = false;
        state.niveaux = action.payload;
        state.error = null;
      })
      .addCase(fetchNiveaux.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Fetch Niveau By ID
      .addCase(fetchNiveauById.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchNiveauById.fulfilled, (state, action) => {
        state.loading = false;
        state.currentNiveau = action.payload;
        state.error = null;
      })
      .addCase(fetchNiveauById.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Create Niveau
      .addCase(createNiveau.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createNiveau.fulfilled, (state, action) => {
        state.loading = false;
        state.niveaux.push(action.payload);
        state.error = null;
      })
      .addCase(createNiveau.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Update Niveau
      .addCase(updateNiveau.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateNiveau.fulfilled, (state, action) => {
        state.loading = false;
        const index = state.niveaux.findIndex(niveau => niveau.id === action.payload.id);
        if (index !== -1) {
          state.niveaux[index] = action.payload;
        }
        state.currentNiveau = action.payload;
        state.error = null;
      })
      .addCase(updateNiveau.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Delete Niveau
      .addCase(deleteNiveau.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteNiveau.fulfilled, (state, action) => {
        state.loading = false;
        state.niveaux = state.niveaux.filter(niveau => niveau.id !== action.payload);
        state.error = null;
      })
      .addCase(deleteNiveau.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export const { clearError, clearCurrentNiveau } = niveauxSlice.actions;
export default niveauxSlice.reducer;
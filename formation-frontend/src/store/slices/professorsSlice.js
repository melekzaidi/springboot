import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import professorsAPI from '../../services/professorsAPI';
import profCourseNiveauxAPI from '../../services/profCourseNiveauxAPI';

// Async thunks
export const fetchProfessors = createAsyncThunk(
  'professors/fetchProfessors',
  async (_, { rejectWithValue }) => {
    try {
      const response = await professorsAPI.getAll();
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération des professeurs');
    }
  }
);

export const fetchProfessorById = createAsyncThunk(
  'professors/fetchProfessorById',
  async (id, { rejectWithValue }) => {
    try {
      const response = await professorsAPI.getById(id);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération du professeur');
    }
  }
);

export const createProfessor = createAsyncThunk(
  'professors/createProfessor',
  async (professorData, { rejectWithValue }) => {
    try {
      const response = await professorsAPI.create(professorData);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la création du professeur');
    }
  }
);

export const updateProfessor = createAsyncThunk(
  'professors/updateProfessor',
  async ({ id, professorData }, { rejectWithValue }) => {
    try {
      const response = await professorsAPI.update(id, professorData);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la modification du professeur');
    }
  }
);

export const deleteProfessor = createAsyncThunk(
  'professors/deleteProfessor',
  async (id, { rejectWithValue }) => {
    try {
      await professorsAPI.delete(id);
      return id;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la suppression du professeur');
    }
  }
);

export const fetchProfCourseNiveaux = createAsyncThunk(
  'professors/fetchProfCourseNiveaux',
  async (_, { rejectWithValue }) => {
    try {
      const response = await profCourseNiveauxAPI.getAll();
      return response.data.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération des affectations');
    }
  }
);

const professorsSlice = createSlice({
  name: 'professors',
  initialState: {
    professors: [],
    profCourseNiveaux: [],
    currentProfessor: null,
    loading: false,
    error: null,
  },
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    clearCurrentProfessor: (state) => {
      state.currentProfessor = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch Professors
      .addCase(fetchProfessors.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchProfessors.fulfilled, (state, action) => {
        state.loading = false;
        state.professors = action.payload;
        state.error = null;
      })
      .addCase(fetchProfessors.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Fetch Professor By ID
      .addCase(fetchProfessorById.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchProfessorById.fulfilled, (state, action) => {
        state.loading = false;
        state.currentProfessor = action.payload;
        state.error = null;
      })
      .addCase(fetchProfessorById.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Create Professor
      .addCase(createProfessor.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createProfessor.fulfilled, (state, action) => {
        state.loading = false;
        state.professors.push(action.payload);
        state.error = null;
      })
      .addCase(createProfessor.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Update Professor
      .addCase(updateProfessor.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateProfessor.fulfilled, (state, action) => {
        state.loading = false;
        const index = state.professors.findIndex(prof => prof.id === action.payload.id);
        if (index !== -1) {
          state.professors[index] = action.payload;
        }
        state.currentProfessor = action.payload;
        state.error = null;
      })
      .addCase(updateProfessor.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Delete Professor
      .addCase(deleteProfessor.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteProfessor.fulfilled, (state, action) => {
        state.loading = false;
        state.professors = state.professors.filter(prof => prof.id !== action.payload);
        state.error = null;
      })
      .addCase(deleteProfessor.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Fetch Prof Course Niveaux
      .addCase(fetchProfCourseNiveaux.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchProfCourseNiveaux.fulfilled, (state, action) => {
        state.loading = false;
        state.profCourseNiveaux = action.payload;
        state.error = null;
      })
      .addCase(fetchProfCourseNiveaux.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export const { clearError, clearCurrentProfessor } = professorsSlice.actions;
export default professorsSlice.reducer;
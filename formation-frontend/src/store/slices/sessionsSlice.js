import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import sessionsAPI from '../../services/sessionsAPI';

// Async thunks
export const fetchSessions = createAsyncThunk(
  'sessions/fetchSessions',
  async (_, { rejectWithValue }) => {
    try {
      const response = await sessionsAPI.getAll();
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération des séances');
    }
  }
);

export const fetchSessionById = createAsyncThunk(
  'sessions/fetchSessionById',
  async (id, { rejectWithValue }) => {
    try {
      const response = await sessionsAPI.getById(id);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération de la séance');
    }
  }
);

export const createSession = createAsyncThunk(
  'sessions/createSession',
  async (sessionData, { rejectWithValue }) => {
    try {
      const response = await sessionsAPI.create(sessionData);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la création de la séance');
    }
  }
);

export const updateSession = createAsyncThunk(
  'sessions/updateSession',
  async ({ id, sessionData }, { rejectWithValue }) => {
    try {
      const response = await sessionsAPI.update(id, sessionData);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la modification de la séance');
    }
  }
);

export const deleteSession = createAsyncThunk(
  'sessions/deleteSession',
  async (id, { rejectWithValue }) => {
    try {
      await sessionsAPI.delete(id);
      return id;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la suppression de la séance');
    }
  }
);

export const fetchSessionsByStudent = createAsyncThunk(
  'sessions/fetchSessionsByStudent',
  async (studentId, { rejectWithValue }) => {
    try {
      const response = await sessionsAPI.getByStudent(studentId);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération des séances');
    }
  }
);

export const fetchSessionsByProf = createAsyncThunk(
  'sessions/fetchSessionsByProf',
  async (profId, { rejectWithValue }) => {
    try {
      const response = await sessionsAPI.getByProf(profId);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération des séances');
    }
  }
);

const sessionsSlice = createSlice({
  name: 'sessions',
  initialState: {
    sessions: [],
    currentSession: null,
    loading: false,
    error: null,
  },
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    clearCurrentSession: (state) => {
      state.currentSession = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch Sessions
      .addCase(fetchSessions.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchSessions.fulfilled, (state, action) => {
        state.loading = false;
        state.sessions = action.payload;
        state.error = null;
      })
      .addCase(fetchSessions.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Fetch Session By ID
      .addCase(fetchSessionById.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchSessionById.fulfilled, (state, action) => {
        state.loading = false;
        state.currentSession = action.payload;
        state.error = null;
      })
      .addCase(fetchSessionById.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Create Session
      .addCase(createSession.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createSession.fulfilled, (state, action) => {
        state.loading = false;
        state.sessions.push(action.payload);
        state.error = null;
      })
      .addCase(createSession.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Update Session
      .addCase(updateSession.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateSession.fulfilled, (state, action) => {
        state.loading = false;
        const index = state.sessions.findIndex(session => session.id === action.payload.id);
        if (index !== -1) {
          state.sessions[index] = action.payload;
        }
        state.currentSession = action.payload;
        state.error = null;
      })
      .addCase(updateSession.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Delete Session
      .addCase(deleteSession.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteSession.fulfilled, (state, action) => {
        state.loading = false;
        state.sessions = state.sessions.filter(session => session.id !== action.payload);
        state.error = null;
      })
      .addCase(deleteSession.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Fetch Sessions By Student
      .addCase(fetchSessionsByStudent.fulfilled, (state, action) => {
        state.sessions = action.payload;
      })
      
      // Fetch Sessions By Prof
      .addCase(fetchSessionsByProf.fulfilled, (state, action) => {
        state.sessions = action.payload;
      });
  },
});

export const { clearError, clearCurrentSession } = sessionsSlice.actions;
export default sessionsSlice.reducer;
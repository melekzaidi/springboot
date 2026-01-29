import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import integratedNotesAPI from '../../services/integratedNotesAPI';

// Async thunks for student data
export const fetchStudentIntegratedData = createAsyncThunk(
  'integratedNotes/fetchStudentIntegratedData',
  async (_, { rejectWithValue }) => {
    try {
      const response = await integratedNotesAPI.getStudentIntegratedData();
      return response.data.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération des données');
    }
  }
);

export const fetchStudentStats = createAsyncThunk(
  'integratedNotes/fetchStudentStats',
  async (_, { rejectWithValue }) => {
    try {
      const response = await integratedNotesAPI.getStudentStats();
      return response.data.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération des statistiques');
    }
  }
);

// Async thunks for professor data
export const fetchProfessorIntegratedData = createAsyncThunk(
  'integratedNotes/fetchProfessorIntegratedData',
  async (_, { rejectWithValue }) => {
    try {
      const response = await integratedNotesAPI.getProfessorIntegratedData();
      return response.data.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération des données');
    }
  }
);

export const fetchProfessorStats = createAsyncThunk(
  'integratedNotes/fetchProfessorStats',
  async (_, { rejectWithValue }) => {
    try {
      const response = await integratedNotesAPI.getProfessorStats();
      return response.data.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération des statistiques');
    }
  }
);

// Grade management thunks
export const createGrade = createAsyncThunk(
  'integratedNotes/createGrade',
  async (gradeData, { rejectWithValue }) => {
    try {
      const response = await integratedNotesAPI.createGrade(gradeData);
      return response.data.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la création de la note');
    }
  }
);

export const updateGrade = createAsyncThunk(
  'integratedNotes/updateGrade',
  async ({ id, gradeData }, { rejectWithValue }) => {
    try {
      const response = await integratedNotesAPI.updateGrade(id, gradeData);
      return response.data.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la modification de la note');
    }
  }
);

export const deleteGrade = createAsyncThunk(
  'integratedNotes/deleteGrade',
  async (id, { rejectWithValue }) => {
    try {
      await integratedNotesAPI.deleteGrade(id);
      return id;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la suppression de la note');
    }
  }
);

// Remark management thunks
export const createRemark = createAsyncThunk(
  'integratedNotes/createRemark',
  async (remarkData, { rejectWithValue }) => {
    try {
      const response = await integratedNotesAPI.createRemark(remarkData);
      return response.data.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la création de la remarque');
    }
  }
);

export const updateRemark = createAsyncThunk(
  'integratedNotes/updateRemark',
  async ({ id, remarkData }, { rejectWithValue }) => {
    try {
      const response = await integratedNotesAPI.updateRemark(id, remarkData);
      return response.data.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la modification de la remarque');
    }
  }
);

export const deleteRemark = createAsyncThunk(
  'integratedNotes/deleteRemark',
  async (id, { rejectWithValue }) => {
    try {
      await integratedNotesAPI.deleteRemark(id);
      return id;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la suppression de la remarque');
    }
  }
);

// Utility thunks
export const fetchGradeTypes = createAsyncThunk(
  'integratedNotes/fetchGradeTypes',
  async (_, { rejectWithValue }) => {
    try {
      const response = await integratedNotesAPI.getGradeTypes();
      return response.data.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération des types de notes');
    }
  }
);

export const fetchAllStudents = createAsyncThunk(
  'integratedNotes/fetchAllStudents',
  async (_, { rejectWithValue }) => {
    try {
      const response = await integratedNotesAPI.getAllStudents();
      return response.data.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération des étudiants');
    }
  }
);

const integratedNotesSlice = createSlice({
  name: 'integratedNotes',
  initialState: {
    // Student data
    studentData: {
      gradesSummary: [],
      professorNotes: [],
      studentInfo: null,
    },
    studentStats: {},
    
    // Professor data
    professorData: {
      grades: [],
      professorNotes: [],
      profCourseNiveaux: [],
      professorInfo: null,
    },
    professorStats: {},
    
    // Utility data
    gradeTypes: [],
    students: [],
    
    // UI state
    loading: false,
    error: null,
    success: null,
  },
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    clearSuccess: (state) => {
      state.success = null;
    },
    setLoading: (state, action) => {
      state.loading = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
      // Student data
      .addCase(fetchStudentIntegratedData.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchStudentIntegratedData.fulfilled, (state, action) => {
        state.loading = false;
        state.studentData = action.payload;
      })
      .addCase(fetchStudentIntegratedData.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Student stats
      .addCase(fetchStudentStats.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchStudentStats.fulfilled, (state, action) => {
        state.loading = false;
        state.studentStats = action.payload;
      })
      .addCase(fetchStudentStats.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Professor data
      .addCase(fetchProfessorIntegratedData.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchProfessorIntegratedData.fulfilled, (state, action) => {
        state.loading = false;
        state.professorData = action.payload;
      })
      .addCase(fetchProfessorIntegratedData.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Professor stats
      .addCase(fetchProfessorStats.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchProfessorStats.fulfilled, (state, action) => {
        state.loading = false;
        state.professorStats = action.payload;
      })
      .addCase(fetchProfessorStats.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Create grade
      .addCase(createGrade.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createGrade.fulfilled, (state, action) => {
        state.loading = false;
        state.success = 'Note créée avec succès';
        state.professorData.grades.push(action.payload);
      })
      .addCase(createGrade.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Update grade
      .addCase(updateGrade.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateGrade.fulfilled, (state, action) => {
        state.loading = false;
        state.success = 'Note modifiée avec succès';
        const index = state.professorData.grades.findIndex(grade => grade.id === action.payload.id);
        if (index !== -1) {
          state.professorData.grades[index] = action.payload;
        }
      })
      .addCase(updateGrade.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Delete grade
      .addCase(deleteGrade.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteGrade.fulfilled, (state, action) => {
        state.loading = false;
        state.success = 'Note supprimée avec succès';
        state.professorData.grades = state.professorData.grades.filter(grade => grade.id !== action.payload);
      })
      .addCase(deleteGrade.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Create remark
      .addCase(createRemark.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createRemark.fulfilled, (state, action) => {
        state.loading = false;
        state.success = 'Remarque créée avec succès';
        state.professorData.professorNotes.push(action.payload);
      })
      .addCase(createRemark.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Update remark
      .addCase(updateRemark.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateRemark.fulfilled, (state, action) => {
        state.loading = false;
        state.success = 'Remarque modifiée avec succès';
        const index = state.professorData.professorNotes.findIndex(note => note.id === action.payload.id);
        if (index !== -1) {
          state.professorData.professorNotes[index] = action.payload;
        }
      })
      .addCase(updateRemark.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Delete remark
      .addCase(deleteRemark.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteRemark.fulfilled, (state, action) => {
        state.loading = false;
        state.success = 'Remarque supprimée avec succès';
        state.professorData.professorNotes = state.professorData.professorNotes.filter(note => note.id !== action.payload);
      })
      .addCase(deleteRemark.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Grade types
      .addCase(fetchGradeTypes.fulfilled, (state, action) => {
        state.gradeTypes = action.payload;
      })
      .addCase(fetchGradeTypes.rejected, (state, action) => {
        state.error = action.payload;
      })
      
      // Students
      .addCase(fetchAllStudents.fulfilled, (state, action) => {
        state.students = action.payload;
      })
      .addCase(fetchAllStudents.rejected, (state, action) => {
        state.error = action.payload;
      });
  },
});

export const { clearError, clearSuccess, setLoading } = integratedNotesSlice.actions;

export default integratedNotesSlice.reducer;
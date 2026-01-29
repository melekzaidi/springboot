import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import studentsAPI from '../../services/studentsAPI';

// Async thunks
export const fetchStudents = createAsyncThunk(
  'students/fetchStudents',
  async (keyword = '', { rejectWithValue }) => {
    try {
      const response = await studentsAPI.getAll(keyword);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération des étudiants');
    }
  }
);

export const fetchStudentById = createAsyncThunk(
  'students/fetchStudentById',
  async (id, { rejectWithValue }) => {
    try {
      const response = await studentsAPI.getById(id);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération de l\'étudiant');
    }
  }
);

export const createStudent = createAsyncThunk(
  'students/createStudent',
  async (studentData, { rejectWithValue }) => {
    try {
      const response = await studentsAPI.create(studentData);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la création de l\'étudiant');
    }
  }
);

export const updateStudent = createAsyncThunk(
  'students/updateStudent',
  async ({ id, studentData }, { rejectWithValue }) => {
    try {
      const response = await studentsAPI.update(id, studentData);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la modification de l\'étudiant');
    }
  }
);

export const deleteStudent = createAsyncThunk(
  'students/deleteStudent',
  async (id, { rejectWithValue }) => {
    try {
      await studentsAPI.delete(id);
      return id;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la suppression de l\'étudiant');
    }
  }
);

export const fetchStudentCount = createAsyncThunk(
  'students/fetchStudentCount',
  async (_, { rejectWithValue }) => {
    try {
      const response = await studentsAPI.getCount();
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors du comptage des étudiants');
    }
  }
);

const studentsSlice = createSlice({
  name: 'students',
  initialState: {
    students: [],
    currentStudent: null,
    totalCount: 0,
    loading: false,
    error: null,
    searchKeyword: '',
  },
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    setSearchKeyword: (state, action) => {
      state.searchKeyword = action.payload;
    },
    clearCurrentStudent: (state) => {
      state.currentStudent = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch Students
      .addCase(fetchStudents.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchStudents.fulfilled, (state, action) => {
        state.loading = false;
        state.students = action.payload;
        state.error = null;
      })
      .addCase(fetchStudents.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Fetch Student By ID
      .addCase(fetchStudentById.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchStudentById.fulfilled, (state, action) => {
        state.loading = false;
        state.currentStudent = action.payload;
        state.error = null;
      })
      .addCase(fetchStudentById.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Create Student
      .addCase(createStudent.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createStudent.fulfilled, (state, action) => {
        state.loading = false;
        state.students.push(action.payload);
        state.error = null;
      })
      .addCase(createStudent.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Update Student
      .addCase(updateStudent.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateStudent.fulfilled, (state, action) => {
        state.loading = false;
        const index = state.students.findIndex(student => student.id === action.payload.id);
        if (index !== -1) {
          state.students[index] = action.payload;
        }
        state.currentStudent = action.payload;
        state.error = null;
      })
      .addCase(updateStudent.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Delete Student
      .addCase(deleteStudent.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteStudent.fulfilled, (state, action) => {
        state.loading = false;
        state.students = state.students.filter(student => student.id !== action.payload);
        state.error = null;
      })
      .addCase(deleteStudent.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Fetch Student Count
      .addCase(fetchStudentCount.fulfilled, (state, action) => {
        state.totalCount = action.payload;
      });
  },
});

export const { clearError, setSearchKeyword, clearCurrentStudent } = studentsSlice.actions;
export default studentsSlice.reducer;
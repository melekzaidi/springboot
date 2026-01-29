import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import professorNotesAPI from '../../services/professorNotesAPI';

// Async thunks for professor actions
export const createNote = createAsyncThunk(
  'professorNotes/createNote',
  async (noteData, { rejectWithValue }) => {
    try {
      const response = await professorNotesAPI.createNote(noteData);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la création de la note');
    }
  }
);

export const updateNote = createAsyncThunk(
  'professorNotes/updateNote',
  async ({ noteId, noteData }, { rejectWithValue }) => {
    try {
      const response = await professorNotesAPI.updateNote(noteId, noteData);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la mise à jour de la note');
    }
  }
);

export const deleteNote = createAsyncThunk(
  'professorNotes/deleteNote',
  async (noteId, { rejectWithValue }) => {
    try {
      await professorNotesAPI.deleteNote(noteId);
      return noteId;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la suppression de la note');
    }
  }
);

export const fetchMyNotes = createAsyncThunk(
  'professorNotes/fetchMyNotes',
  async (_, { rejectWithValue }) => {
    try {
      const response = await professorNotesAPI.getMyNotes();
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération des notes');
    }
  }
);

export const fetchNotesByStudent = createAsyncThunk(
  'professorNotes/fetchNotesByStudent',
  async (studentId, { rejectWithValue }) => {
    try {
      const response = await professorNotesAPI.getNotesByStudent(studentId);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération des notes');
    }
  }
);

export const fetchNotesByCourse = createAsyncThunk(
  'professorNotes/fetchNotesByCourse',
  async (profCourseNiveauId, { rejectWithValue }) => {
    try {
      const response = await professorNotesAPI.getNotesByCourse(profCourseNiveauId);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération des notes');
    }
  }
);

// Async thunks for student actions
export const fetchMyStudentNotes = createAsyncThunk(
  'professorNotes/fetchMyStudentNotes',
  async (_, { rejectWithValue }) => {
    try {
      const response = await professorNotesAPI.getMyStudentNotes();
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération des notes');
    }
  }
);

export const fetchMyStudentNotesInCourse = createAsyncThunk(
  'professorNotes/fetchMyStudentNotesInCourse',
  async (profCourseNiveauId, { rejectWithValue }) => {
    try {
      const response = await professorNotesAPI.getMyStudentNotesInCourse(profCourseNiveauId);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération des notes');
    }
  }
);

// Bulk operations
export const createBulkNotes = createAsyncThunk(
  'professorNotes/createBulkNotes',
  async (notesData, { rejectWithValue }) => {
    try {
      const response = await professorNotesAPI.createBulkNotes(notesData);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la création des notes');
    }
  }
);

export const updateBulkNotes = createAsyncThunk(
  'professorNotes/updateBulkNotes',
  async (notesData, { rejectWithValue }) => {
    try {
      const response = await professorNotesAPI.updateBulkNotes(notesData);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la mise à jour des notes');
    }
  }
);

export const deleteBulkNotes = createAsyncThunk(
  'professorNotes/deleteBulkNotes',
  async (noteIds, { rejectWithValue }) => {
    try {
      await professorNotesAPI.deleteBulkNotes(noteIds);
      return noteIds;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la suppression des notes');
    }
  }
);

const professorNotesSlice = createSlice({
  name: 'professorNotes',
  initialState: {
    notes: [],
    studentNotes: [],
    loading: false,
    error: null,
    selectedNote: null,
  },
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    clearNotes: (state) => {
      state.notes = [];
      state.studentNotes = [];
    },
    setSelectedNote: (state, action) => {
      state.selectedNote = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
      // Create note
      .addCase(createNote.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createNote.fulfilled, (state, action) => {
        state.loading = false;
        state.notes.unshift(action.payload.data);
      })
      .addCase(createNote.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Update note
      .addCase(updateNote.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateNote.fulfilled, (state, action) => {
        state.loading = false;
        const index = state.notes.findIndex(note => note.id === action.payload.data.id);
        if (index !== -1) {
          state.notes[index] = action.payload.data;
        }
      })
      .addCase(updateNote.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Delete note
      .addCase(deleteNote.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteNote.fulfilled, (state, action) => {
        state.loading = false;
        state.notes = state.notes.filter(note => note.id !== action.payload);
      })
      .addCase(deleteNote.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Fetch my notes (professor)
      .addCase(fetchMyNotes.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchMyNotes.fulfilled, (state, action) => {
        state.loading = false;
        state.notes = action.payload.data;
      })
      .addCase(fetchMyNotes.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Fetch notes by student
      .addCase(fetchNotesByStudent.fulfilled, (state, action) => {
        state.loading = false;
        state.notes = action.payload.data;
      })
      
      // Fetch notes by course
      .addCase(fetchNotesByCourse.fulfilled, (state, action) => {
        state.loading = false;
        state.notes = action.payload.data;
      })
      
      // Fetch my student notes
      .addCase(fetchMyStudentNotes.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchMyStudentNotes.fulfilled, (state, action) => {
        state.loading = false;
        state.studentNotes = action.payload.data;
      })
      .addCase(fetchMyStudentNotes.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      // Fetch my student notes in course
      .addCase(fetchMyStudentNotesInCourse.fulfilled, (state, action) => {
        state.loading = false;
        state.studentNotes = action.payload.data;
      })
      
      // Bulk operations
      .addCase(createBulkNotes.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createBulkNotes.fulfilled, (state, action) => {
        state.loading = false;
        state.notes = [...action.payload.data, ...state.notes];
      })
      .addCase(createBulkNotes.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      .addCase(updateBulkNotes.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateBulkNotes.fulfilled, (state, action) => {
        state.loading = false;
        action.payload.data.forEach(updatedNote => {
          const index = state.notes.findIndex(note => note.id === updatedNote.id);
          if (index !== -1) {
            state.notes[index] = updatedNote;
          }
        });
      })
      .addCase(updateBulkNotes.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      
      .addCase(deleteBulkNotes.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteBulkNotes.fulfilled, (state, action) => {
        state.loading = false;
        state.notes = state.notes.filter(note => !action.payload.includes(note.id));
      })
      .addCase(deleteBulkNotes.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export const { clearError, clearNotes, setSelectedNote } = professorNotesSlice.actions;
export default professorNotesSlice.reducer;
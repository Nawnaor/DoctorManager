package project.kyawmyoag.doctormanager.Note;


public class MyNote {

    String TitleNote;
    String Description;
    String Priority;
    String NoteId;
    String Date;

    public MyNote (){

    }

    public MyNote (String TitleNote,String Description,String Date,String Priority, String NoteId) {
        this.TitleNote = TitleNote;
        this.Description = Description;
        this.Priority = Priority;
        this.Date = Date;
        this.NoteId = NoteId;

    }

    public String getNoteId() {
        return NoteId;
    }
    public void setNoteId(String NoteId) {
        this.NoteId = NoteId;
    }

    public String getTitleNote() {
        return TitleNote;
    }
    public void setTitleNote(String TitleNote) {
        this.TitleNote = TitleNote;
    }

    public String getDescription() {
        return Description;
    }
    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getPriority() {
        return Priority;
    }
    public void setPriority(String addPriority) {
        this.Priority=addPriority;
    }

    public String getDate() {
        return Date;
    }
    public void setDate(String addDate) {
        this.Date = addDate;
    }
}

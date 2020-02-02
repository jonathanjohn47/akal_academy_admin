package com.iiysoftware.academy.TeacherAttendance;

public class TeacherAttendanceModel {
    private String teacherName, teacherID, attendanceValue;

    public TeacherAttendanceModel(String teacherName, String teacherID, String attendanceValue) {
        this.teacherName = teacherName;
        this.teacherID = teacherID;
        this.attendanceValue = attendanceValue;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public String getAttendanceValue() {
        return attendanceValue;
    }
}

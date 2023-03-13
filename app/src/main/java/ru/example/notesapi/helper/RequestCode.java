package ru.example.notesapi.helper;

/**
 * Class for implementation storing requestCodes for Activities
 */
public class RequestCode {
    private static int code;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

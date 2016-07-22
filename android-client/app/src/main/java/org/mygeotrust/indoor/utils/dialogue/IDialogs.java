package org.mygeotrust.indoor.utils.dialogue;

/**
 * Created by Dr. Mahbubul Syeed on 22.7.2016.
 */
public interface IDialogs {
    enum SelectionStatus{
        ok_pressed,
        cancel_pressed
    }
    void onDialogOptionSelected(SelectionStatus status);
}

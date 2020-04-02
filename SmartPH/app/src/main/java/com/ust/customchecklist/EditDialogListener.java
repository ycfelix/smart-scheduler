package com.ust.customchecklist;

import org.jetbrains.annotations.NotNull;

public interface EditDialogListener {
    public void onEditResult(@NotNull DataModel data, RequestType type);
}

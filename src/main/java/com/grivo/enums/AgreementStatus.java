package com.grivo.enums;

public enum AgreementStatus {
    ACTIVE,          // tenant currently living there
    MOVE_OUT_PENDING, // move-out photos not yet complete
    CLOSED,           // move-out documented, no dispute raised
    DISPUTED          // dispute raised, unresolved
}

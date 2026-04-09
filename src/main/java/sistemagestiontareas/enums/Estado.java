package sistemagestiontareas.enums;

/**
 * Representa los posibles estados de una tarea dentro del sistema
 */
public enum Estado {

    /** La tarea aún no ha comenzado */
    PENDIENTE,

    /** La tarea está en proceso */
    EN_PROGRESO,

    /** La tarea ha sido finalizada */
    COMPLETADA,

    /** La tarea fue cancelada */
    CANCELADA
}
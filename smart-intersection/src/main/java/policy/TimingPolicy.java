package policy;

/**
 * Interface TimingPolicy — SOLID: Open/Closed Principle
 * Define el contrato para distintas políticas de tiempo del semáforo.
 * Permite agregar nuevas políticas sin modificar el CycleController.
 */
public interface TimingPolicy {
    int getGreenTime();
    int getRedTime();
}

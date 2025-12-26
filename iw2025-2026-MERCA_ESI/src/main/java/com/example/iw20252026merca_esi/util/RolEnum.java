package com.example.iw20252026merca_esi.util;

/**
 * Enum con los roles disponibles en el sistema.
 * Usar estos valores en lugar de strings mágicos para evitar errores tipográficos.
 */
public enum RolEnum {
    ADMINISTRADOR("ADMINISTRADOR"),
    EMPLEADO("EMPLEADO"),
    REPARTIDOR("REPARTIDOR"),
    MANAGER("MANAGER"),
    PROPIETARIO("PROPIETARIO"),
    COCINA("COCINA");

    private final String nombre;

    RolEnum(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}

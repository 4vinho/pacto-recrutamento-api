package br.com.pacto.recrutamento.app.ports.curriculo;

import br.com.pacto.recrutamento.app.serviceImpl.CurriculoServiceImpl;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CurriculoArchitectureTest {

    @Test
    void casoDeUsoEPortasFormamUmSliceNaAplicacao() {
        assertThat(CurriculoServiceImpl.class.getPackage().getName())
                .isEqualTo("br.com.pacto.recrutamento.app.serviceImpl");

        assertThat(Arrays.stream(CurriculoServiceImpl.class.getDeclaredFields())
                .map(Field::getType)
                .filter(Class::isInterface)
                .map(type -> type.getPackage().getName()))
                .allMatch("br.com.pacto.recrutamento.app.ports.curriculo"::equals);
    }

}

package br.com.pacto.recrutamento.app.ports.out.curriculo;

import br.com.pacto.recrutamento.app.usecases.curriculo.CurriculoService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CurriculoArchitectureTest {

    @Test
    void casoDeUsoEPortasFormamUmSliceNaAplicacao() {
        assertThat(CurriculoService.class.getPackage().getName())
                .isEqualTo("br.com.pacto.recrutamento.app.usecases.curriculo");

        assertThat(Arrays.stream(CurriculoService.class.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .map(Field::getType)
                .filter(Class::isInterface)
                .map(type -> type.getPackage().getName()))
                .allMatch(nome -> nome.startsWith("br.com.pacto.recrutamento.app.ports.out."));
    }

}

package br.com.pacto.recrutamento.app.dtos.candidatura;

import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class ResumoCandidaturasDTO {
    private final long total;
    private final Map<StatusCandidatura, Long> totaisPorStatus;

    public ResumoCandidaturasDTO(Map<StatusCandidatura, Long> totaisPorStatus) {
        EnumMap<StatusCandidatura, Long> copia = new EnumMap<>(StatusCandidatura.class);
        for (StatusCandidatura status : StatusCandidatura.values()) {
            copia.put(status, totaisPorStatus.getOrDefault(status, 0L));
        }
        this.totaisPorStatus = Collections.unmodifiableMap(copia);
        this.total = copia.values().stream().mapToLong(Long::longValue).sum();
    }
    public long getTotal() { return total; }
    public Map<StatusCandidatura, Long> getTotaisPorStatus() { return totaisPorStatus; }
}

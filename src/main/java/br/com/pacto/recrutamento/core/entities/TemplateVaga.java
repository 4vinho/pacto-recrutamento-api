package br.com.pacto.recrutamento.core.entities;

import java.time.OffsetDateTime;
import java.util.UUID;

public class TemplateVaga {
    private UUID id = UUID.randomUUID(); private UUID responsavelId; private String titulo; private String descricao;
    private OffsetDateTime criadoEm; private OffsetDateTime atualizadoEm; private OffsetDateTime excluidoEm;
    public TemplateVaga() { }
    public TemplateVaga(UUID responsavelId, String titulo, String descricao) { this.responsavelId=responsavelId; this.titulo=titulo; this.descricao=descricao; }
    public UUID getId(){return id;} public void setId(UUID id){this.id=id;} public UUID getResponsavelId(){return responsavelId;} public void setResponsavelId(UUID x){responsavelId=x;} public String getTitulo(){return titulo;} public void setTitulo(String x){titulo=x;} public String getDescricao(){return descricao;} public void setDescricao(String x){descricao=x;} public OffsetDateTime getCriadoEm(){return criadoEm;} public void setCriadoEm(OffsetDateTime x){criadoEm=x;} public OffsetDateTime getAtualizadoEm(){return atualizadoEm;} public void setAtualizadoEm(OffsetDateTime x){atualizadoEm=x;} public OffsetDateTime getExcluidoEm(){return excluidoEm;} public void setExcluidoEm(OffsetDateTime x){excluidoEm=x;}
}

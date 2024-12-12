package ru.kpfu.translationapi.domain.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.kpfu.translationapi.domain.entity.Translation;

import java.util.List;

@Repository
public class TranslationDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TranslationDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Translation> findAll() {
        String sql = "SELECT * FROM translation";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Translation(
                rs.getLong("id"),
                rs.getString("source_language"),
                rs.getString("target_language"),
                rs.getString("source_text"),
                rs.getString("translated_text")
        ));
    }

    public Translation findById(Long id) {
        String sql = "SELECT * FROM translations WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> new Translation(
                rs.getLong("id"),
                rs.getString("source_language"),
                rs.getString("target_language"),
                rs.getString("source_text"),
                rs.getString("translated_text")
        ));
    }

    public int save(Translation translation) {
        String sql = "INSERT INTO translations (source_language, target_language, source_text, translated_text) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                translation.getSourceLanguage(),
                translation.getTargetLanguage(),
                translation.getSourceText(),
                translation.getTranslatedText());
    }

    public int update(Translation translation) {
        String sql = "UPDATE translations SET source_language = ?, target_language = ?, source_text = ?, translated_text = ? WHERE id = ?";
        return jdbcTemplate.update(sql,
                translation.getSourceLanguage(),
                translation.getTargetLanguage(),
                translation.getSourceText(),
                translation.getTranslatedText(),
                translation.getId());
    }

    public int delete(Long id) {
        String sql = "DELETE FROM translations WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

}

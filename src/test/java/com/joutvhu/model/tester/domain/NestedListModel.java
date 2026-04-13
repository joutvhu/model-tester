package com.joutvhu.model.tester.domain;

import java.util.List;
import java.util.Objects;

public class NestedListModel {
    private SimplePojo info;
    private Double score;
    private Long timestamp;
    private float ratio;
    private List<String> tags;

    public NestedListModel() {
    }

    public NestedListModel(SimplePojo info) {
        this.info = info;
    }

    public SimplePojo getInfo() {
        return info;
    }

    public void setInfo(SimplePojo info) {
        this.info = info;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NestedListModel that = (NestedListModel) o;

        if (Float.compare(that.ratio, ratio) != 0) return false;
        if (!Objects.equals(info, that.info)) return false;
        if (!Objects.equals(score, that.score)) return false;
        if (!Objects.equals(timestamp, that.timestamp)) return false;
        return Objects.equals(tags, that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(info, score, timestamp, ratio, tags);
    }

    @Override
    public String toString() {
        return "NestedListModel{" +
                "info=" + info +
                ", score=" + score +
                ", timestamp=" + timestamp +
                ", ratio=" + ratio +
                ", tags=" + tags +
                '}';
    }
}

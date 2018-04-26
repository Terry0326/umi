package com.ugoodtech.umi.core.domain;

/* Copyright (C) Ugoodtech, Inc - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 * Written by Stone Shaw.
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ugoodtech.umi.core.domain.converter.BlockActionConverter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "block_users")
public class BlockUser extends BaseEntity {
    private User executor;
    private User blocker;
    private BlockAction action;

    @ManyToOne
    @JoinColumn(name = "executor_id")
    public User getExecutor() {
        return executor;
    }

    public void setExecutor(User executor) {
        this.executor = executor;
    }

    @ManyToOne
    @JoinColumn(name = "blocker_id")
    public User getBlocker() {
        return blocker;
    }

    public void setBlocker(User blocker) {
        this.blocker = blocker;
    }

    @Convert(converter = BlockActionConverter.class)
    public BlockAction getAction() {
        return action;
    }

    public void setAction(BlockAction action) {
        this.action = action;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum BlockAction {
        NOOP(0, "无"), BLOCK(1, "拉黑"), DONT_SEE_ITS_TOPICS(1 << 1, "不看他的帖子"), BLOCK_AND_NOT_SEE_ITS_TOPICS(1 << 1 | 1, "拉黑且不看他的帖子");
        private Integer code;
        private String description;

        BlockAction(Integer code, String description) {
            this.code = code;
            this.description = description;
        }

        @JsonProperty
        public Integer getCode() {
            return code;
        }

        @JsonProperty
        public String getDescription() {
            return description;
        }

        public static final Map<Integer, BlockAction> dbValues = new HashMap<>();

        static {
            for (BlockAction action : values()) {
                dbValues.put(action.getCode(), action);
            }
        }

        @JsonCreator
        public static BlockAction forValue(Integer value) {
            return dbValues.get(value);
        }

        public BlockAction merge(BlockAction block) {
            Integer newCode = this.getCode() | block.getCode();
            return forValue(newCode);
        }

        public BlockAction unmerge(BlockAction block) {
            Integer newCode = this.getCode() & (~block.getCode()) ;
            return forValue(newCode);
        }
    }

}
package com.gn128.enums;

import com.gn128.exception.payloads.BadRequestException;
import org.springframework.http.HttpStatus;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.enums
 * Created_on - December 03 - 2024
 * Created_at - 19:32
 */

public enum RelationshipGoal {

    DATING("Dating"),
    FRIENDSHIP("Friendship"),
    CASUAL("Casual"),
    OPEN_TO_CHAT("Open to Chat"),
    SERIOUS_RELATIONSHIP("Serious Relationship");

    private final String value;

    RelationshipGoal(String value) {
        this.value = value;
    }

    public static RelationshipGoal getByValue(String value) {
        for (RelationshipGoal relationshipGoal : values()) {
            if (relationshipGoal.value.equals(value)) {
                return relationshipGoal;
            }
        }
        throw new BadRequestException("Relationship Goal value is incorrect", HttpStatus.BAD_REQUEST);
    }
}

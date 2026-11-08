package domain.validator;

import domain.Friendship;
import domain.User;


public class FriendshipValidation  implements Validation<Friendship> {

    @Override
    public void validate(Friendship entity) throws ValidationException {
        if (entity == null) {
            throw new ValidationException("prietenia nu poate fi goala");
        }

        User firstFriend = entity.getFriend1();
        User secondFriend = entity.getFriend2();

        if (firstFriend == null || secondFriend == null) {
            throw new ValidationException("prietenii nu pot fi goli");
        }

        if (firstFriend.getId().equals(secondFriend.getId())) {
            throw new ValidationException("Un om nu poate fi prieten cu el insusi");
        }
    }
}

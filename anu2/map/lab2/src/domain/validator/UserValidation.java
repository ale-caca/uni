package domain.validator;

import domain.User;

public class UserValidation implements Validation<User>{
    @Override
    public void validate(User entity){
        if(entity.getFirstName().isEmpty() || entity.getLastName().isEmpty()){
            throw new ValidationException("Numele si prenumele nu pot fi goale");
        }
    }

}

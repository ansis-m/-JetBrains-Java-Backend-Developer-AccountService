package account.numberService;

import account.numberService.NumberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NumberServiceImp {

    private NumberRepository numberRepository;

    @Autowired
    public NumberServiceImp(NumberRepository numberRepository) {
        this.numberRepository = numberRepository;
    }


}

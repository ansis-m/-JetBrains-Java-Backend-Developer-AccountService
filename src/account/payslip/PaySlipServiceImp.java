package account.payslip;

import account.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaySlipServiceImp implements PaySlipService {


    @Autowired
    PaySlipRepository paySlipRepository;


    @Override
    public List<PaySlip> getAll() {
        return paySlipRepository.findAll();
    }

    @Override
    public void save(PaySlip paySlip) {
        paySlipRepository.save(paySlip);

    }

    @Override
    public PaySlip findById(Long id) {
        return paySlipRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        paySlipRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        paySlipRepository.deleteAll();
    }
}

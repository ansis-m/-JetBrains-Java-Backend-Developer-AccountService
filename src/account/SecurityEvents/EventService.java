package account.SecurityEvents;

import account.payslip.PaySlip;

import java.util.List;

public interface EventService {

    List<Event> getAll();
    void save(Event event);
    Event findById(Long id);
    void deleteById(Long id);
    void deleteAll();
}

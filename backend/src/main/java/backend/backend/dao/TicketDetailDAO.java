package backend.backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;


import backend.backend.entity.TicketDetail;

public interface TicketDetailDAO extends JpaRepository<TicketDetail, Integer> {

}

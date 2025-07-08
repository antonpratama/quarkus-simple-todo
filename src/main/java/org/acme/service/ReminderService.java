package org.acme.service;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.scheduler.Scheduled;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.acme.entity.Todo;

import java.time.LocalDateTime;
import java.util.List;

@Singleton
public class ReminderService {

  @Inject
  Mailer mailer;

  @Scheduled(every = "1m")
  public void checkReminders(){
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime reminderThreshold = now.plusMinutes(5);

    List<Todo> upcoming = Todo.find("completed = false and dueDate BETWEEN ?1 and ?2", now, reminderThreshold).list();

    for (Todo todo : upcoming){
      System.out.printf("🔔 Reminder! Todo '%s' due at %s%n", todo.title, todo.dueDate);

      //kirim email
//      mailer.send(Mail.withText(
//              "antnpra@gmail.com",
//              "Reminder: " + todo.title,
//              "Todo anda dengan judul '" + todo.title + "' akan jatuh tempo pada " + todo.dueDate
//      ));
    }
  }
}

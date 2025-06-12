package booking.system.hovedopgave.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // These fields store date and time values representing UTC instants.
    // LocalDateTime itself does not hold timezone info, but the system treats these as UTC based on frontend input.
    // When sending data via the API, these values are converted to OffsetDateTime with explicit UTC offset to ensure clarity.
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String location;
    private Integer maxParticipants;
    private Boolean isAvailable;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private OfferedService offeredService;
}


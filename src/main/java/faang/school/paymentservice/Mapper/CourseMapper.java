package faang.school.paymentservice.Mapper;

import faang.school.paymentservice.dto.CourseDto;
import faang.school.paymentservice.dto.RatesDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CourseMapper {

    public List<CourseDto> convert(RatesDto ratesDto) {
        if (ratesDto == null || ratesDto.getRates() == null) {
            return null;
        }

        return ratesDto.getRates().entrySet().stream()
                .map(entry -> new CourseDto(
                        ratesDto.base,
                        entry.getKey(),
                        entry.getValue())
                )
                .toList();
    }
}

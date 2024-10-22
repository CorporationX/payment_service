package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.client.ProjectDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "project-service", url = "${project-service.host}:${project-service.port}")
public interface ProjectServiceClient {

    @GetMapping("/project/{projectId}")
    ProjectDto getProject(@PathVariable long projectId);
}

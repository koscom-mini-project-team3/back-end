package koscom.mini3.domain.deposit.api;

import koscom.mini3.domain.deposit.application.DepositService;
import koscom.mini3.domain.deposit.dto.DepositResponseDto;
import koscom.mini3.deposit.util.TestDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DepositControllerTest {

    @Mock
    private DepositService depositService;

    @InjectMocks
    private DepositAPI depositController;

    private MockMvc mockMvc;

    private List<DepositResponseDto> mockDepositDtos;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(depositController).build();
        mockDepositDtos = TestDataUtil.createMockDeposits().stream().map(DepositResponseDto::new).toList();
    }

    @Test
    void getDepositsSortedByBaseRate_ShouldReturnOk() throws Exception {
        when(depositService.getDepositsSortedByBaseRate(anyInt(), anyLong())).thenReturn(mockDepositDtos);

        mockMvc.perform(get("/api/deposits/baserate?term=12&minAmount=1000000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(mockDepositDtos.size()));
    }

    @Test
    void getDepositsSortedByHighRate_ShouldReturnOk() throws Exception {
        when(depositService.getDepositsSortedByHighRate(anyInt(), anyLong())).thenReturn(mockDepositDtos);

        mockMvc.perform(get("/api/deposits/highrate?term=12&minAmount=1000000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(mockDepositDtos.size()));
    }

    @Test
    void getDepositById_ShouldReturnOk() throws Exception {
        when(depositService.getDepositById(1L)).thenReturn(mockDepositDtos.get(0));

        mockMvc.perform(get("/api/deposits/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
}

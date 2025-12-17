package com.jty.pf.bindasynctransaction.restAPI.data;

import com.jty.pf.bindasynctransaction.utils.TypeConverter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IpResponseDTOTest {

    @Test
    void name() throws Exception {
        IpResponseDTO from = IpResponseDTO.from("TEST-01", ResultCode.SUCCESS, 1L);
        String json = TypeConverter.toJson(from);
        System.out.println("json = " + json);

        IpResponseDTO ipResponseDTO = IpResponseDTO.from(json);

        Assertions.assertThat(ipResponseDTO.getResultCode()).isEqualTo(ResultCode.SUCCESS);
    }
}
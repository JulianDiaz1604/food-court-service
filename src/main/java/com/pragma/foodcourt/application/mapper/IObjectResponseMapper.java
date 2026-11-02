package com.pragma.foodcourt.application.mapper;

import com.pragma.foodcourt.application.dto.response.ObjectResponseDto;
import com.pragma.foodcourt.domain.model.ObjectModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IObjectResponseMapper {
    ObjectResponseDto toResponse(ObjectModel objectModel);

    List<ObjectResponseDto> toResponseList(List<ObjectModel> objectModelList);
}

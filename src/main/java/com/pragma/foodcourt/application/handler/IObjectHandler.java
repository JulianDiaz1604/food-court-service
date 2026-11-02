package com.pragma.foodcourt.application.handler;

import com.pragma.foodcourt.application.dto.request.ObjectRequestDto;
import com.pragma.foodcourt.application.dto.response.ObjectResponseDto;

import java.util.List;

public interface IObjectHandler {

    void saveObject(ObjectRequestDto objectRequestDto);

    List<ObjectResponseDto> getAllObjects();
}
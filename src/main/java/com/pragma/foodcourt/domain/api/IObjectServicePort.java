package com.pragma.foodcourt.domain.api;

import com.pragma.foodcourt.domain.model.ObjectModel;

import java.util.List;

public interface IObjectServicePort {

    void saveObject(ObjectModel objectModel);

    List<ObjectModel> getAllObjects();
}
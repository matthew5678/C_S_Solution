package com.example.Code_Sharing_Solution.Repository;

import com.example.Code_Sharing_Solution.Entities.Code;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeRepositoryStringID extends CrudRepository<Code,String> {

    @Override
    <S extends Code> S save(S entity);
}

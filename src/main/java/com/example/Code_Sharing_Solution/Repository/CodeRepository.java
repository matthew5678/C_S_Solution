package com.example.Code_Sharing_Solution.Repository;

import com.example.Code_Sharing_Solution.Entities.Code;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeRepository extends CrudRepository<Code,Long> {

    @Override
    <S extends Code> S save(S entity);
}

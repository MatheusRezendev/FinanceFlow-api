package org.example.financeflowapi.domain.model.repository.service.exception.service;

import org.example.financeflowapi.domain.model.repository.service.exception.exception.ResourceBadRequestException;
import org.example.financeflowapi.domain.model.repository.service.exception.exception.ResourceNotFoundException;
import org.example.financeflowapi.domain.model.repository.service.exception.model.Usuario;
import org.example.financeflowapi.domain.model.repository.service.exception.repository.UsuarioRepository;
import org.example.financeflowapi.dto.usuario.titulo.centrodecusto.usuario.UsuarioRequestDto;
import org.example.financeflowapi.dto.usuario.titulo.centrodecusto.usuario.UsuarioResponseDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService implements ICRUDService<UsuarioRequestDto, UsuarioResponseDto>{

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    public List<UsuarioResponseDto> obterTodos() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        List<UsuarioResponseDto> usuariosDto = new ArrayList<>();
        for(Usuario usuario : usuarios){
            UsuarioResponseDto dto = (mapper.map(usuario, UsuarioResponseDto.class));
            usuariosDto.add(dto);
        }

        return usuariosDto;
    }

    @Override
    public UsuarioResponseDto obterPorId(Long id) {
        Optional<Usuario> optUsuario = usuarioRepository.findById(id);

        if(optUsuario.isEmpty()){
            throw new ResourceNotFoundException("Nao foi possivel encontrar o usuario com o ID: " + id);
        }

        return mapper.map(optUsuario.get(), UsuarioResponseDto.class);
    }

    @Override
    public UsuarioResponseDto cadastrar(UsuarioRequestDto dto) {

        validarUsuario(dto);

        Optional<Usuario> optUsuario = usuarioRepository.findByEmail(dto.getEmail());

        if(optUsuario.isPresent()){
            throw new ResourceBadRequestException("Esse e-mail ja esta vinculado a um usuario: " + dto.getEmail());
        }

        Usuario usuario = mapper.map(dto, Usuario.class);
        //adicionar encoder na senha

        usuario.setId(null);
        usuario.setDataCadastro(new Date());
        usuario = usuarioRepository.save(usuario);

        return mapper.map(usuario, UsuarioResponseDto.class);
    }

    @Override
    public UsuarioResponseDto atualizar(Long id, UsuarioRequestDto dto) {

        UsuarioResponseDto usuarioBanco = obterPorId(id);
        validarUsuario(dto);

        Usuario usuario = mapper.map(dto, Usuario.class);
        //adicionar encoder na senha

        usuario.setId(id);
        usuario.setDataInativacao(usuarioBanco.getDataInativacao());
        usuario.setDataCadastro(usuarioBanco.getDataCadastro());

        usuario = usuarioRepository.save(usuario);
        return mapper.map(usuario, UsuarioResponseDto.class);
    }

    @Override
    public void remover(Long id) {

        Optional<Usuario> optUsuario = usuarioRepository.findById(id);

        if(optUsuario.isEmpty()){
            throw new ResourceNotFoundException("Nao foi possivel encontrar o usuario com o ID: " + id);
        }

        Usuario usuario = optUsuario.get();
        usuario.setDataInativacao(new Date());
        usuarioRepository.save(usuario);
    }

    private void validarUsuario(UsuarioRequestDto dto) {

        if(dto.getEmail() == null || dto.getSenha() == null){
            throw new ResourceBadRequestException("E-mail e senha sao obrigatorios");
        }
    }
}

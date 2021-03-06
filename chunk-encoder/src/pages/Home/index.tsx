import React, { useEffect, useState } from 'react'
import { 
  Box, 
  Heading, 
  useColorModeValue, 
  Drawer, 
  DrawerContent, 
  Button, 
  Divider, 
  useDisclosure, 
  Popover, 
  PopoverContent, 
  Portal,
  PopoverHeader,
  PopoverArrow,
  PopoverCloseButton,
  PopoverFooter,
  ButtonGroup
} from '@chakra-ui/react'
import Editor from '../../components/elements/Editor'
import Sidebar from '../../components/features/Sidebar'
import MobileNav from '../../components/features/MobileNav'
import ChunkList from '../../components/elements/ChunkList'
import { onCreate, onDelete, onSave, getChunks } from '../../api'
import { Chunk } from '../../types'

const EDITOR_CHUNK = 'editor-chunk'

const defaultChunk: Chunk = {
  id: '201051ff-e936-4f5e-9cc4-364240f0af30',
  title: 'title',
  body: `# First go

example list

- [ ] one
- [ ] two
- [ ] three
- [ ] four
- [ ] five
`,
  createdAt: new Date('2022-02-13T16:53:42.947Z'),
  updatedAt: new Date('2022-02-13T16:53:42.947Z')
}

function initialChunk(): Chunk {
  const storageChunk = window.localStorage.getItem(EDITOR_CHUNK)
  if (storageChunk) {
    return JSON.parse(storageChunk)
  } else {
    return defaultChunk
  }
}

function initialChunks(): Chunk[] {
  return []
}

function App() {
  const { isOpen, onOpen, onClose } = useDisclosure()
  const [isPopoverOpen, setIsPopoverOpen] = React.useState(false)
  const [popoverLoc, setPopoverLoc] = useState({ left: 0, top: 0 })
  const [readOnly, setReadOnly] = useState(true)

  const [chunk, setChunk] = useState<Chunk>(initialChunk())
  const [initEditorValue, setInitEditorValue] = useState<Chunk>(initialChunk())
  const [chunks, setChunks] = useState(initialChunks())

  useEffect(() => {
    window.localStorage.setItem(EDITOR_CHUNK, JSON.stringify(chunk))
    getChunks().then(returnedChunks => setChunks(returnedChunks)) 
  }, [chunk]);

  return (
    <Box minH="100vh" bg={useColorModeValue('gray.100', 'gray.900')}>
      {/* NAVIGATION */}
      <Sidebar onClose={() => onClose()} display={{ base: 'none', md: 'block' }}>
        <ChunkList chunks={chunks} selected={chunk.id} onClick={ch => { setChunk(ch); setInitEditorValue(ch) }} />
      </Sidebar>
      <Drawer
        autoFocus={false}
        isOpen={isOpen}
        placement="left"
        onClose={onClose}
        returnFocusOnClose={false}
        onOverlayClick={onClose}
        size="full">
        <DrawerContent>
          <Sidebar onClose={onClose}>
            <ChunkList chunks={chunks} selected={chunk.id} onClick={ch => { setChunk(ch); setInitEditorValue(ch); onClose() }} />
          </Sidebar>
        </DrawerContent>
      </Drawer>
      <MobileNav display={{ base: 'flex', md: 'none' }} onOpen={onOpen} />

      {/* MAIN WINDOW */}
      <Box ml={{ base: 0, md: 80 }} p="4">
        <Heading>{chunk.title}</Heading>

        <Editor
          initValue={initEditorValue.body}
          onMouseUp={(text, loc) => {
            // HERE WE HAVE ACCESS TO HIGHLIGHTED TEXT
            if (readOnly && text) { // We should only do this if in Read Only Mode
              setPopoverLoc({ top: loc?.bottom || 0, left: loc?.left || 0 })
              setIsPopoverOpen(true)
            }
          }}
          readOnly={readOnly}
          onChange={(currentChunkBody: () => any) => {
            const title = currentChunkBody().split('\n')[0].replace(/^#*\s/, '')
            const isValidTitle = title.length && !!title.replace(/\s/g, '').length

            setChunk({
              ...chunk,
              title: isValidTitle ? title : chunk.id,
              body: currentChunkBody()
            })
          }}
        />

        <Popover 
          isOpen={isPopoverOpen} 
          onClose={() => setIsPopoverOpen(false)} 
          strategy={'absolute'}
        >
          <Portal>
          <PopoverContent color='white' bg='blue.800' borderColor='blue.800' sx={{left: popoverLoc.left, top: popoverLoc.top  }}>
            <PopoverHeader pt={4} fontWeight='bold' border='0'>
              Create a Flash Card
            </PopoverHeader>
            <PopoverArrow />
            <PopoverCloseButton />
            <PopoverFooter
              border='0'
              d='flex'
              alignItems='center'
              justifyContent='space-between'
              pb={4}
            >
              <ButtonGroup size='sm'>
                <Button colorScheme='green'>Clozed Card</Button>
              </ButtonGroup>
            </PopoverFooter>
          </PopoverContent>
          </Portal>
        </Popover>

        <Divider />
        <div className='control'>
          <Button colorScheme='blue' onClick={() => { onCreate().then(chunk => { setChunk(chunk); setInitEditorValue(chunk); }) }}>New</Button>
          <Button colorScheme='green' onClick={() => { onSave(chunk) }}>Save</Button>
          <Button colorScheme='red' onClick={() => { onDelete(chunk.id).then(_ => {setChunk(chunks[0]); setInitEditorValue(chunks[0]) }) }}>Delete</Button>
          <Button colorScheme='yellow' onClick={() => { setReadOnly(!readOnly) }}>{ readOnly?'Edit':'Read' }</Button>
        </div>
      </Box>
    </Box>
  )
}

export default App;

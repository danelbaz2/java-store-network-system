# Store Management System - Client-Server Architecture

## Planning Phase
- [x] Create implementation plan for client-server architecture
- [x] User confirmed: Replace ConsoleMenu, JSON storage, chat last

## Phase 1: Server & Client Core
- [ ] Create protocol package (Request.java, Response.java)
- [ ] Create server package (StoreServer.java, ClientHandler.java, ServerApp.java)
- [ ] Create client package (BranchClient.java, BranchMenu.java, ClientApp.java)

## Phase 2: Authentication
- [ ] Implement LOGIN/LOGOUT via server
- [ ] Update Chain with synchronized session management

## Phase 3: Inventory & Sales
- [ ] Implement GET_INVENTORY request
- [ ] Implement SELL_PRODUCT request (synchronized)
- [ ] Update StoreBranch for server-side operations

## Phase 4: JSON Data Persistence
- [ ] Create JsonDataStore utility
- [ ] Save/load employees to JSON
- [ ] Save/load inventory to JSON
- [ ] Save/load customers to JSON

## Phase 5: Chat (Last Priority)
- [ ] Implement SEND_MESSAGE request
- [ ] Broadcast messages between branches

## Verification
- [ ] Test 2 clients simultaneously
- [ ] Test concurrent sales (race condition)
- [ ] Test data persistence after restart
